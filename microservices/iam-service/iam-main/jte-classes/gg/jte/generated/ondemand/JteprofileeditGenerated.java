package gg.jte.generated.ondemand;
import java.util.Map;
import kh.edu.istad.iam.application.result.UserResult;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JteprofileeditGenerated {
	public static final String JTE_NAME = "profile-edit.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,2,2,2,2,9,9,9,9,9,9,9,9,9,9,9,9,9,10,10,10,10,10,10,11,11,11,11,11,11,11,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,12,14,14,14,14,14,14,14,14,14,14,14,14,15,15,15,15,15,15,15,15,15,15,15,15,16,16,16,16,16,16,16,16,16,16,16,16,17,17,17,17,17,17,17,17,17,17,17,17,18,18,18,18,18,18,18,18,18,18,18,18,19,19,19,19,19,19,19,19,19,19,19,19,20,20,20,20,20,20,20,20,20,20,20,20,21,21,21,21,21,21,21,21,21,22,22,22,22,22,22,22,22,22,22,22,22,23,23,23,23,23,23,23,23,23,23,23,23,24,24,24,24,24,24,24,24,24,24,24,24,25,25,25,25,25,25,25,25,25,25,25,25,26,26,26,26,26,26,26,26,26,26,26,26,27,27,27,27,27,27,27,27,27,27,27,27,28,28,28,28,28,28,28,28,28,28,28,28,29,29,29,29,29,29,29,29,29,29,29,29,30,30,30,30,30,30,30,30,30,30,30,30,31,31,31,32,32,32,2,3,4,5,6,7,7,7,7};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, Map<String, String> messages, String localeTag, String csrfParameterName, String csrfToken, UserResult user, boolean saved) {
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
		jteOutput.writeUserContent(messages.get("profile.title"));
		jteOutput.writeContent("</title><link rel=\"stylesheet\" href=\"/css/iam.css\"></head>\n<body><header><a href=\"/\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("common.home"));
		jteOutput.writeContent("</a></header><main><section class=\"card\"><h1>");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(messages.get("profile.title"));
		jteOutput.writeContent("</h1>\n");
		if (saved) {
			jteOutput.writeContent("<p class=\"notice\">");
			jteOutput.setContext("p", null);
			jteOutput.writeUserContent(messages.get("common.success"));
			jteOutput.writeContent("</p>");
		}
		jteOutput.writeContent("\n<form action=\"/profile/edit\" method=\"post\"><input type=\"hidden\"");
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
		jteOutput.writeContent(">\n<div class=\"grid\">\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.givenName"));
		jteOutput.writeContent("</label><input name=\"givenName\"");
		var __jte_html_attribute_3 = user.profile().givenName() == null ? "" : user.profile().givenName();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_3)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_3);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.familyName"));
		jteOutput.writeContent("</label><input name=\"familyName\"");
		var __jte_html_attribute_4 = user.profile().familyName() == null ? "" : user.profile().familyName();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_4)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_4);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.middleName"));
		jteOutput.writeContent("</label><input name=\"middleName\"");
		var __jte_html_attribute_5 = user.profile().middleName() == null ? "" : user.profile().middleName();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_5)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_5);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.nickname"));
		jteOutput.writeContent("</label><input name=\"nickname\"");
		var __jte_html_attribute_6 = user.profile().nickname() == null ? "" : user.profile().nickname();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_6)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_6);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.gender"));
		jteOutput.writeContent("</label><input name=\"gender\"");
		var __jte_html_attribute_7 = user.profile().gender() == null ? "" : user.profile().gender();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_7)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_7);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.birthdate"));
		jteOutput.writeContent("</label><input name=\"birthdate\" type=\"date\"");
		var __jte_html_attribute_8 = user.profile().birthdate() == null ? "" : user.profile().birthdate().toString();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_8)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_8);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.phoneNumber"));
		jteOutput.writeContent("</label><input name=\"phoneNumber\"");
		var __jte_html_attribute_9 = user.profile().phoneNumber() == null ? "" : user.profile().phoneNumber();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_9)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_9);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.locale"));
		jteOutput.writeContent("</label><select name=\"locale\"><option value=\"en\">");
		jteOutput.setContext("option", null);
		jteOutput.writeUserContent(messages.get("common.english"));
		jteOutput.writeContent("</option><option value=\"km\">");
		jteOutput.setContext("option", null);
		jteOutput.writeUserContent(messages.get("common.khmer"));
		jteOutput.writeContent("</option></select></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.profileUrl"));
		jteOutput.writeContent("</label><input name=\"profileUrl\"");
		var __jte_html_attribute_10 = user.profile().profileUrl() == null ? "" : user.profile().profileUrl();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_10)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_10);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.pictureUrl"));
		jteOutput.writeContent("</label><input name=\"pictureUrl\"");
		var __jte_html_attribute_11 = user.profile().pictureUrl() == null ? "" : user.profile().pictureUrl();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_11)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_11);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.websiteUrl"));
		jteOutput.writeContent("</label><input name=\"websiteUrl\"");
		var __jte_html_attribute_12 = user.profile().websiteUrl() == null ? "" : user.profile().websiteUrl();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_12)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_12);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.zoneinfo"));
		jteOutput.writeContent("</label><input name=\"zoneinfo\"");
		var __jte_html_attribute_13 = user.profile().zoneinfo() == null ? "" : user.profile().zoneinfo();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_13)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_13);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.streetAddress"));
		jteOutput.writeContent("</label><input name=\"streetAddress\"");
		var __jte_html_attribute_14 = user.address().streetAddress() == null ? "" : user.address().streetAddress();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_14)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_14);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.locality"));
		jteOutput.writeContent("</label><input name=\"locality\"");
		var __jte_html_attribute_15 = user.address().locality() == null ? "" : user.address().locality();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_15)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_15);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.region"));
		jteOutput.writeContent("</label><input name=\"region\"");
		var __jte_html_attribute_16 = user.address().region() == null ? "" : user.address().region();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_16)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_16);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.postalCode"));
		jteOutput.writeContent("</label><input name=\"postalCode\"");
		var __jte_html_attribute_17 = user.address().postalCode() == null ? "" : user.address().postalCode();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_17)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_17);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n<div><label>");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("profile.country"));
		jteOutput.writeContent("</label><input name=\"country\" maxlength=\"2\"");
		var __jte_html_attribute_18 = user.address().country() == null ? "" : user.address().country();
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_18)) {
			jteOutput.writeContent(" value=\"");
			jteOutput.setContext("input", "value");
			jteOutput.writeUserContent(__jte_html_attribute_18);
			jteOutput.setContext("input", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent("></div>\n</div><p><button>");
		jteOutput.setContext("button", null);
		jteOutput.writeUserContent(messages.get("profile.save"));
		jteOutput.writeContent("</button></p></form></section></main></body></html>\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		Map<String, String> messages = (Map<String, String>)params.get("messages");
		String localeTag = (String)params.get("localeTag");
		String csrfParameterName = (String)params.get("csrfParameterName");
		String csrfToken = (String)params.get("csrfToken");
		UserResult user = (UserResult)params.get("user");
		boolean saved = (boolean)params.get("saved");
		render(jteOutput, jteHtmlInterceptor, messages, localeTag, csrfParameterName, csrfToken, user, saved);
	}
}
