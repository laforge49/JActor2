package org.agilewiki.jactor2.kdriver;

import org.agilewiki.jactor2.core.messaging.AsyncResponseProcessor;
import org.agilewiki.jactor2.core.messaging.ExceptionHandler;
import org.agilewiki.jactor2.osgi.FactoriesImporter;
import org.agilewiki.jactor2.osgi.FactoryLocatorActivator;
import org.agilewiki.jactor2.osgi.LocateService;
import org.agilewiki.jactor2.testIface.Hello;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Hashtable;

public class Activator extends FactoryLocatorActivator {
    private final Logger log = LoggerFactory.getLogger(Activator.class);
    private CommandProcessor commandProcessor;

    @Override
    protected void process() throws Exception {
        log.warn("wait for commands to load");
        Thread.sleep(10000);
        getMessageProcessor().setExceptionHandler(new ExceptionHandler() {
            @Override
            public Void processException(Exception exception) throws Exception {
                log.error("test failure", exception);
                getFacility().close();
                return null;
            }
        });
        LocateService<CommandProcessor> locateService = new LocateService(getMessageProcessor(),
                CommandProcessor.class.getName());
        locateService.getReq().send(getMessageProcessor(), new AsyncResponseProcessor<CommandProcessor>() {
            @Override
            public void processAsyncResponse(CommandProcessor response) throws Exception {
                commandProcessor = response;
                managedServiceRegistration();
                test1();
            }
        });
    }

    protected String executeCommands(final String... commands) throws Exception {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        final CommandSession commandSession = commandProcessor.createSession(System.in, printStream, System.err);
        String cmds = "";
        try {
            for (String command : commands) {
                log.info(command);
                cmds = cmds + command + " || ";
                commandSession.execute(command);
            }
        } catch (Exception e) {
            log.error(cmds, e);
        }
        return byteArrayOutputStream.toString();
    }

    void test1() throws Exception {
        log.info(">>>>>>>>1>>>>>>>>>> " + executeCommands(
                "config:edit org.agilewiki.jactor2.testService.Activator." + getVersion().toString(),
                "config:propset msg Aloha!",
                "config:propset import_a jactor2-osgi\\|" + getNiceVersion(),
                "config:update"));
        String bundleLocation = "mvn:org.agilewiki.jactor2/jactor2-test-service/" + getNiceVersion();
        FactoriesImporter factoriesImporter = new FactoriesImporter(getMessageProcessor());
        factoriesImporter.startReq(bundleLocation).send(getMessageProcessor(), new AsyncResponseProcessor<Void>() {
            @Override
            public void processAsyncResponse(Void response) throws Exception {
                LocateService<Hello> locateService = new LocateService(getMessageProcessor(), Hello.class.getName());
                locateService.getReq().send(getMessageProcessor(), new AsyncResponseProcessor<Hello>() {
                    @Override
                    public void processAsyncResponse(Hello response) throws Exception {
                        //log.info(">>>>>>>>>>>>>>>>>> "+executeCommands("osgi:ls", "config:list"));
                        String r = response.getMessage();
                        if (!"Aloha!".equals(r)) {
                            log.error("Unexpected response from Hello.getMessage(): " + r);
                            getFacility().close();
                            return;
                        }
                        success();
                    }
                });
            }
        });
    }

    void success() throws Exception {
        Hashtable<String, String> p = new Hashtable<String, String>();
        p.put("kdriverSuccess", "true");
        bundleContext.registerService(
                KDriverSuccess.class.getName(),
                new KDriverSuccess(),
                p);
    }
}
