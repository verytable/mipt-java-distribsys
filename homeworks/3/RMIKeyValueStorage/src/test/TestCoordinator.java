package test;

import coordinator.Coordinator;
import coordinator.ViewInfo;

/**
 * Created by arseny on 16.11.14.
 */

class TestFailedException extends Exception {
    TestFailedException(String message) {
        super(message);
    }
}

public class TestCoordinator {

    static void test(ViewInfo info,
                     String primary,
                     String backup,
                     int view,
                     String description) throws TestFailedException {
        if (!primary.equals(info.primary)) {
            System.err.println("Wrong primary: expected " + primary +
                    ", got " + info.primary);
            throw new TestFailedException(description);
        }
        if (!backup.equals(info.backup)) {
            System.err.println("Wrong backup: expected " + backup +
                    ", got " + info.backup);
            throw new TestFailedException(description);
        }
        if (view != info.view) {
            System.err.println("Wrong view number: expected " + view +
                    ", got " + info.view);
            throw new TestFailedException(description);
        }
        System.err.println("Test passed: " + description);
    }

    public static void main(String[] argv) throws Exception {
        Coordinator service = new Coordinator();
        int longDelay = Coordinator.deadPings * 2;
        String srv1 = "localhost:10001";
        String srv2 = "localhost:10002";
        String srv3 = "localhost:10003";
        int currentView = 0;
        ViewInfo info = null;

        try {
            // no ready servers
            if (!service.primary().equals(""))
                throw new TestFailedException("no ready servers");
            System.err.println(info);
            System.err.println("Test passed: no ready servers\n");

            // first primary
            for (int i = 0; i < longDelay; ++i) {
                info = service.ping(0, srv1);
                if (info.view == currentView + 1) break;
                service.tick();
            }
            ++currentView;
            System.err.println(info);
            test(info, srv1, "", currentView, "first primary\n");

            // first backup
            for (int i = 0; i < longDelay; ++i) {
                service.ping(currentView, srv1);
                info = service.ping(0, srv2);
                if (info.view == currentView + 1) break;
                service.tick();
            }
            ++currentView;
            System.err.println(info);
            test(info, srv1, srv2, currentView, "first backup\n");

            // primary fails, backup should take over
            service.ping(2, srv1);
            for (int i = 0; i < longDelay; ++i) {
                info = service.ping(2, srv2);
                if (info.view == currentView + 1) break;
                service.tick();
            }
            ++currentView;
            System.err.println(info);
            test(info, srv2, "", currentView, "backup takes over\n");

            // first server restarts, should become backup
            for (int i = 0; i < longDelay; ++i) {
                service.ping(currentView, srv2);
                info = service.ping(0, srv1);
                if (info.view == currentView + 1) break;
                service.tick();
            }
            ++currentView;
            System.err.println(info);
            test(info, srv2, srv1, currentView, "restarted server becomes backup\n");

            // primary fails, third server appears,
            // backup should become primary, new server - backup
            service.ping(currentView, srv2);
            for (int i = 0; i < longDelay; ++i) {
                service.ping(currentView, srv1);
                info = service.ping(0, srv3);
                if (info.view == currentView + 1) break;
                service.tick();
            }
            ++currentView;
            System.err.println(info);
            test(info, srv1, srv3, currentView, "spare server becomes backup\n");

            // primary quickly restarts, should not be primary anymore
            service.ping(currentView, srv1);
            for (int i = 0; i < longDelay; ++i) {
                service.ping(0, srv1);
                info = service.ping(currentView, srv3);
                if (info.view == currentView + 1) break;
                service.tick();
            }
            ++currentView;
            System.err.println(info);
            test(info, srv3, srv1, currentView, "primary reboots");
            System.err.println("Test passed: primary reboots\n");

            // set up a view with just 3 as primary,
            // to prepare for the next test.
            for (int i = 0; i < longDelay; ++i) {
                info = service.ping(currentView, srv3);
                service.tick();
            }
            ++currentView;
            System.err.println(info);
            test(info, srv3, "", currentView, "primary only\n");

            // backup appears but primary does not ack
            for (int i = 0; i < longDelay; ++i) {
                service.ping(0, srv1);
                info = service.ping(currentView, srv3);
                if (info.view == currentView + 1) break;
                service.tick();
            }
            ++currentView;
            System.err.println(info);
            test(info, srv3, srv1, currentView, "primary doesn't ack\n");

            // primary didn't ack and dies
            // check that backup is not promoted
            for (int i = 0; i < longDelay; ++i) {
                info = service.ping(currentView, srv1);
                if (info.view == currentView + 1) break;
                service.tick();
            }
            System.err.println(info);
            test(info, srv3, srv1, currentView, "do not promote backup\n");

        } catch (TestFailedException e) {
            System.err.println("Test failed: " + e.getMessage());
        }
    }
}
