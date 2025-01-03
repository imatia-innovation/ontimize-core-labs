package com.ontimize.jee.desktopclient.test;


import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;

import com.ontimize.jee.common.callback.CallbackWrapperMessage;
import com.ontimize.jee.common.services.servermanagement.IServerManagementService;
import com.ontimize.jee.common.services.user.IUserInformationService;
import com.ontimize.jee.common.services.user.UserInformation;
import com.ontimize.jee.common.tools.ThreadTools;
import com.ontimize.jee.desktopclient.callback.ICallbackClientHandler;
import com.ontimize.jee.desktopclient.callback.ICallbackEventListener;
import com.ontimize.jee.desktopclient.spring.BeansFactory;

public class CallbackTest extends AbstractIdentifiedCallbackOntimizeTest {

	private static final Logger logger = LoggerFactory.getLogger(CallbackTest.class);

	public static void main(final String[] args) {
		try {
			new CallbackTest().prepareTest(args);
		} catch (final Exception error) {
			CallbackTest.logger.error(null, error);
		}
	}

	protected ICallbackEventListener createCallbackManager() {
		((GenericApplicationContext) BeansFactory.getApplicationContext())
		.registerBean(ICallbackEventListener.class, (Supplier) () -> {
			return new CallbackManager();
		});
		return BeansFactory.getBean(ICallbackEventListener.class);
	}

	@Override
	protected void doTest() {
		createCallbackManager();
		final IUserInformationService service = BeansFactory.getBean(IUserInformationService.class);
		final UserInformation userInformation = service.getUserInformation();
		System.out.println(userInformation);

		final IServerManagementService management = this.createService(IServerManagementService.class,
				"/private/services/hessian/serverManagement");
		System.out.println(management.getAvailableDataSources());
		ThreadTools.sleep(300000);
		System.out.println("Finalizado");
	}

	public static class CallbackManager implements ICallbackEventListener, ApplicationContextAware {

		public CallbackManager() {
			super();
		}

		@Override
		public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
			final ICallbackClientHandler callbackClientHandler = applicationContext
					.getBean(ICallbackClientHandler.class);
			callbackClientHandler.addCallbackEventListener(this);
		}

		/**
		 * Receive events from WebSocket notifying new INPUT or new OUTPUT
		 */
		@Override
		public void onCallbackMessageReceived(final CallbackWrapperMessage message) {
			logger.info("Event received: {}", message);
		}
	}

}
