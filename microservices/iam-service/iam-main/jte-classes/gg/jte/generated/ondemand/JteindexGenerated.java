package gg.jte.generated.ondemand;
import java.util.Map;
import java.util.List;
import kh.edu.istad.iam.application.result.UserResult;
import kh.edu.istad.iam.application.result.IdentityResult;
import kh.edu.istad.iam.application.result.ConnectedApplicationResult;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JteindexGenerated {
	public static final String JTE_NAME = "index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,3,4,5,5,5,5,5,13,13,13,13,13,13,13,13,13,13,13,13,13,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,18,18,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,19,20,20,21,21,21,22,22,24,24,24,25,25,25,26,26,26,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,32,32,32,5,6,7,8,9,10,11,11,11,11};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, Map<String, String> messages, String localeTag, String csrfParameterName, String csrfToken, UserResult user, List<IdentityResult> identities, List<ConnectedApplicationResult> applications) {
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
		jteOutput.writeUserContent(messages.get("index.profile"));
		jteOutput.writeContent("</title><link rel=\"stylesheet\" href=\"/css/iam.css\"></head>\n<body><header><a href=\"/\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("common.brand"));
		jteOutput.writeContent("</a><nav><a href=\"/profile/edit\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("profile.edit"));
		jteOutput.writeContent("</a><a href=\"/profile/accounts\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("profile.linkedAccounts"));
		jteOutput.writeContent("</a><a href=\"/profile/apps\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("profile.connectedApps"));
		jteOutput.writeContent("</a><form action=\"/logout\" method=\"post\"><input type=\"hidden\"");
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
		jteOutput.writeUserContent(messages.get("common.logout"));
		jteOutput.writeContent("</button></form></nav></header>\n<main>\n<section class=\"card profile-header\">\n    <div class=\"profile-info-row\">\n        ");
		if (user.profile() != null && user.profile().pictureUrl() != null && !user.profile().pictureUrl().isBlank()) {
			jteOutput.writeContent("\n        <img class=\"avatar\"");
			var __jte_html_attribute_3 = user.profile().pictureUrl();
			if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_3)) {
				jteOutput.writeContent(" src=\"");
				jteOutput.setContext("img", "src");
				jteOutput.writeUserContent(__jte_html_attribute_3);
				jteOutput.setContext("img", null);
				jteOutput.writeContent("\"");
			}
			var __jte_html_attribute_4 = user.fullName();
			if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_4)) {
				jteOutput.writeContent(" alt=\"");
				jteOutput.setContext("img", "alt");
				jteOutput.writeUserContent(__jte_html_attribute_4);
				jteOutput.setContext("img", null);
				jteOutput.writeContent("\"");
			}
			jteOutput.writeContent(">\n        ");
		} else {
			jteOutput.writeContent("\n        <div class=\"avatar avatar-placeholder\">");
			jteOutput.setContext("div", null);
			jteOutput.writeUserContent(user.fullName().substring(0, 1).toUpperCase());
			jteOutput.writeContent("</div>\n        ");
		}
		jteOutput.writeContent("\n        <div class=\"profile-meta\">\n            <h1>");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(user.fullName());
		jteOutput.writeContent("</h1>\n            <p class=\"user-email\">");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(user.email());
		jteOutput.writeContent("</p>\n            <p class=\"user-username\">@@");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(user.username());
		jteOutput.writeContent("</p>\n        </div>\n    </div>\n</section>\n<div class=\"grid\"><section class=\"card\"><h2>");
		jteOutput.setContext("h2", null);
		jteOutput.writeUserContent(messages.get("profile.linkedAccounts"));
		jteOutput.writeContent("</h2><p>");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(identities.size());
		jteOutput.writeContent("</p><a href=\"/profile/accounts\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("common.home"));
		jteOutput.writeContent("</a></section><section class=\"card\"><h2>");
		jteOutput.setContext("h2", null);
		jteOutput.writeUserContent(messages.get("profile.connectedApps"));
		jteOutput.writeContent("</h2><p>");
		jteOutput.setContext("p", null);
		jteOutput.writeUserContent(applications.size());
		jteOutput.writeContent("</p><a href=\"/profile/apps\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("profile.connectedApps"));
		jteOutput.writeContent("</a></section><section class=\"card\"><h2>");
		jteOutput.setContext("h2", null);
		jteOutput.writeUserContent(messages.get("profile.changePassword"));
		jteOutput.writeContent("</h2><a href=\"/profile/password\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("profile.changePassword"));
		jteOutput.writeContent("</a></section></div></main>\n</body></html>\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		Map<String, String> messages = (Map<String, String>)params.get("messages");
		String localeTag = (String)params.get("localeTag");
		String csrfParameterName = (String)params.get("csrfParameterName");
		String csrfToken = (String)params.get("csrfToken");
		UserResult user = (UserResult)params.get("user");
		List<IdentityResult> identities = (List<IdentityResult>)params.get("identities");
		List<ConnectedApplicationResult> applications = (List<ConnectedApplicationResult>)params.get("applications");
		render(jteOutput, jteHtmlInterceptor, messages, localeTag, csrfParameterName, csrfToken, user, identities, applications);
	}
}
