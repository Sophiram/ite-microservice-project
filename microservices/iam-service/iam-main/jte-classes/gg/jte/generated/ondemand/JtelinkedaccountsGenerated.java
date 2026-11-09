package gg.jte.generated.ondemand;
import java.util.Map;
import java.util.List;
import kh.edu.istad.iam.application.result.IdentityResult;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JtelinkedaccountsGenerated {
	public static final String JTE_NAME = "linked-accounts.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,3,3,3,3,3,9,9,9,9,9,9,9,9,9,9,9,9,9,10,10,10,10,10,10,11,11,11,11,11,11,11,12,12,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,13,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,15,15,17,17,17,3,4,5,6,7,7,7,7};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, Map<String, String> messages, String localeTag, String csrfParameterName, String csrfToken, List<IdentityResult> identities) {
		jteOutput.writeContent("<!doctype html>\n<html");
		var __jte_html_attribute_0 = localeTag;
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_0)) {
			jteOutput.writeContent(" lang=\"");
			jteOutput.setContext("html", "lang");
			jteOutput.writeUserContent(__jte_html_attribute_0);
			jteOutput.setContext("html", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><title>");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(messages.get("profile.linkedAccounts"));
		jteOutput.writeContent("</title><link rel=\"stylesheet\" href=\"/css/iam.css\"></head>\n<body><header><a href=\"/\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("common.home"));
		jteOutput.writeContent("</a></header><main><section class=\"card\"><h1>");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(messages.get("profile.linkedAccounts"));
		jteOutput.writeContent("</h1>\n");
		if (identities.isEmpty()) {
			jteOutput.writeContent("<p class=\"muted\">");
			jteOutput.setContext("p", null);
			jteOutput.writeUserContent(messages.get("identity.none"));
			jteOutput.writeContent("</p>");
		}
		jteOutput.writeContent("\n");
		for (IdentityResult identity : identities) {
			jteOutput.writeContent("\n<article class=\"row\"><h2>");
			jteOutput.setContext("h2", null);
			jteOutput.writeUserContent(identity.provider());
			jteOutput.writeContent("</h2><p>");
			jteOutput.setContext("p", null);
			jteOutput.writeUserContent(messages.get("identity.email"));
			jteOutput.writeContent(": ");
			jteOutput.setContext("p", null);
			jteOutput.writeUserContent(identity.email() == null ? "" : identity.email());
			jteOutput.writeContent("</p><p>");
			jteOutput.setContext("p", null);
			jteOutput.writeUserContent(messages.get("identity.displayName"));
			jteOutput.writeContent(": ");
			jteOutput.setContext("p", null);
			jteOutput.writeUserContent(identity.displayName() == null ? "" : identity.displayName());
			jteOutput.writeContent("</p><p>");
			jteOutput.setContext("p", null);
			jteOutput.writeUserContent(messages.get("identity.linkedAt"));
			jteOutput.writeContent(": ");
			jteOutput.setContext("p", null);
			jteOutput.writeUserContent(identity.linkedAt().toString());
			jteOutput.writeContent("</p>\n<form action=\"/profile/accounts/");
			jteOutput.setContext("form", "action");
			jteOutput.writeUserContent(identity.uuid().toString());
			jteOutput.setContext("form", null);
			jteOutput.writeContent("/unlink\" method=\"post\"><input type=\"hidden\"");
			var __jte_html_attribute_1 = csrfParameterName;
			if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_1)) {
				jteOutput.writeContent(" name=\"");
				jteOutput.setContext("input", "name");
				jteOutput.writeUserContent(__jte_html_attribute_1);
				jteOutput.setContext("input", null);
				jteOutput.writeContent("\"");
			}
			var __jte_html_attribute_2 = csrfToken;
			if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_2)) {
				jteOutput.writeContent(" value=\"");
				jteOutput.setContext("input", "value");
				jteOutput.writeUserContent(__jte_html_attribute_2);
				jteOutput.setContext("input", null);
				jteOutput.writeContent("\"");
			}
			jteOutput.writeContent("><button class=\"secondary\">");
			jteOutput.setContext("button", null);
			jteOutput.writeUserContent(messages.get("identity.unlink"));
			jteOutput.writeContent("</button></form></article>\n");
		}
		jteOutput.writeContent("\n</section></main></body></html>\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		Map<String, String> messages = (Map<String, String>)params.get("messages");
		String localeTag = (String)params.get("localeTag");
		String csrfParameterName = (String)params.get("csrfParameterName");
		String csrfToken = (String)params.get("csrfToken");
		List<IdentityResult> identities = (List<IdentityResult>)params.get("identities");
		render(jteOutput, jteHtmlInterceptor, messages, localeTag, csrfParameterName, csrfToken, identities);
	}
}
