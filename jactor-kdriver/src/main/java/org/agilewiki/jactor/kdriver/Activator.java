package org.agilewiki.jactor.kdriver;

import org.agilewiki.jactor.api.*;
import org.agilewiki.jactor.testIface.Hello;
import org.agilewiki.jactor.util.osgi.MailboxFactoryActivator;
import org.agilewiki.jactor.util.osgi.serviceTracker.LocateService;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Hashtable;

public class Activator extends MailboxFactoryActivator {
    private Mailbox mailbox;
    private final Logger log = LoggerFactory.getLogger(Activator.class);
    private CommandProcessor commandProcessor;
    private String niceVersion;

    @Override
    public void start(final BundleContext _bundleContext) throws Exception {
        super.start(_bundleContext);
        mailbox = getMailboxFactory().createMailbox();
        startReq().signal();
    }

    Request<Void> startReq() {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(final Transport<Void> _transport) throws Exception {
                niceVersion = niceVersion(bundleContext.getBundle().getVersion());
                mailbox.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(Throwable throwable) throws Throwable {
                        _transport.processResponse(null);
                        log.error("test failure", throwable);
                        getMailboxFactory().close();
                    }
                });
                LocateService<CommandProcessor> locateService = new LocateService(mailbox,
                        CommandProcessor.class.getName());
                locateService.getReq().send(mailbox, new ResponseProcessor<CommandProcessor>() {
                    @Override
                    public void processResponse(CommandProcessor response) throws Exception {
                        commandProcessor = response;
                        test1(_transport);
                    }
                });
            }
        };
    }

    protected String executeCommands(final String ...commands) throws Exception {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        final CommandSession commandSession = commandProcessor.createSession(System.in, printStream, System.err);
        String cmds = "";
        try {
            for(String command:commands) {
                log.info(command);
                cmds = cmds + command + " || ";
                commandSession.execute(command);
            }
        } catch (Exception e) {
            log.error(cmds, e);
        }
        return byteArrayOutputStream.toString();
    }

    void test1(final Transport<Void> t) throws Exception {
        log.info(">>>>>>>>>>>>>>>>>> "+executeCommands("features:listUrl"));
        log.info(">>>>>>>>>>>>>>>>>> "+executeCommands("features:list", "config:list"));
        final Bundle service = bundleContext.installBundle("mvn:org.agilewiki.jactor/JActor-test-service/" + niceVersion);
        service.start();
        LocateService<Hello> locateService = new LocateService(mailbox, Hello.class.getName());
        locateService.getReq().send(mailbox, new ResponseProcessor<Hello>() {
            @Override
            public void processResponse(Hello response) throws Exception {
                String r = response.getMessage();
                if (!"Hello Pax!".equals(r)) {
                    t.processResponse(null);
                    log.error("Unexpected response from Hello.getMessage(): " + r);
                    getMailboxFactory().close();
                }
                service.stop();
                service.uninstall();
                success(t);
            }
        });
    }

    void success(final Transport<Void> t) throws Exception {
        t.processResponse(null);
        Hashtable<String, String> p = new Hashtable<String, String>();
        p.put("kdriverSuccess", "true");
        bundleContext.registerService(
                KDriverSuccess.class.getName(),
                new KDriverSuccess(),
                p);
    }
}
