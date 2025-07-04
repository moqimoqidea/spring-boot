/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package smoketest.webservices.endpoint;

import java.time.LocalDate;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import smoketest.webservices.service.HumanResourceService;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;

@Endpoint
public class HolidayEndpoint {

	private static final String NAMESPACE_URI = "https://company.example.com/hr/schemas";

	private final XPathExpression<Element> startDateExpression;

	private final XPathExpression<Element> endDateExpression;

	private final XPathExpression<String> nameExpression;

	private final HumanResourceService humanResourceService;

	public HolidayEndpoint(HumanResourceService humanResourceService) {
		this.humanResourceService = humanResourceService;
		Namespace namespace = Namespace.getNamespace("hr", NAMESPACE_URI);
		XPathFactory xPathFactory = XPathFactory.instance();
		this.startDateExpression = xPathFactory.compile("//hr:StartDate", Filters.element(), null, namespace);
		this.endDateExpression = xPathFactory.compile("//hr:EndDate", Filters.element(), null, namespace);
		this.nameExpression = xPathFactory.compile("concat(//hr:FirstName,' ',//hr:LastName)", Filters.fstring(), null,
				namespace);
	}

	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "HolidayRequest")
	public void handleHolidayRequest(@RequestPayload Element holidayRequest) {
		LocalDate startDate = LocalDate.parse(this.startDateExpression.evaluateFirst(holidayRequest).getText());
		LocalDate endDate = LocalDate.parse(this.endDateExpression.evaluateFirst(holidayRequest).getText());
		String name = this.nameExpression.evaluateFirst(holidayRequest);
		this.humanResourceService.bookHoliday(startDate, endDate, name);
	}

}
