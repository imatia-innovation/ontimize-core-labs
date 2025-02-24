package com.ontimize.jee.desktopclient.test.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.services.servermanagement.IServerManagementService;
import com.ontimize.jee.common.services.user.IUserInformationService;
import com.ontimize.jee.common.services.user.UserInformation;
import com.ontimize.jee.desktopclient.spring.BeansFactory;
import com.ontimize.jee.desktopclient.test.AbstractIdentifiedOntimizeTest;

public class SessionTest extends AbstractIdentifiedOntimizeTest {

	private static final Logger logger = LoggerFactory.getLogger(SessionTest.class);

	public static void main(final String[] args) {
		try {
			new SessionTest().prepareTest(args);
		} catch (final Exception error) {
			SessionTest.logger.error(null, error);
		}
	}

	@Override
	protected void doTest() {

		final IUserInformationService service = BeansFactory.getBean(IUserInformationService.class);
		final UserInformation userInformation = service.getUserInformation();
		System.out.println(userInformation);

		final IServerManagementService management = this.createService(IServerManagementService.class,
				"/private/services/hessian/serverManagement");
		System.out.println(management.getAvailableDataSources());


		System.out.println("Finalizado");
	}

}
