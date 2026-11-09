package gg.jte.generated.ondemand;
import java.util.Map;
import java.util.List;
@SuppressWarnings("unchecked")
@javax.annotation.processing.Generated("gg.jte.TemplateEngine")
public final class JteloginGenerated {
	public static final String JTE_NAME = "login.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,2,2,2,2,10,10,10,10,10,10,10,10,10,10,11,11,11,14,14,14,15,15,16,16,16,17,17,18,18,19,19,19,20,20,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,22,23,23,23,24,24,24,25,25,25,28,28,29,29,29,29,29,29,29,30,30,32,32,32,33,33,33,33,33,33,50,50,50,2,3,4,5,6,7,8,8,8,8};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, Map<String, String> messages, String localeTag, String csrfParameterName, String csrfToken, List<String> providerIds, boolean error, boolean registered) {
		jteOutput.writeContent("<!doctype html>\n<html");
		var __jte_html_attribute_0 = localeTag;
		if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_0)) {
			jteOutput.writeContent(" lang=\"");
			jteOutput.setContext("html", "lang");
			jteOutput.writeUserContent(__jte_html_attribute_0);
			jteOutput.setContext("html", null);
			jteOutput.writeContent("\"");
		}
		jteOutput.writeContent(">\n    <head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"><title>");
		jteOutput.setContext("title", null);
		jteOutput.writeUserContent(messages.get("login.title"));
		jteOutput.writeContent("</title><link rel=\"stylesheet\" href=\"/css/iam.css\"></head>\n    <body>\n        <main class=\"narrow\"><section class=\"card\">\n            <h1>");
		jteOutput.setContext("h1", null);
		jteOutput.writeUserContent(messages.get("login.title"));
		jteOutput.writeContent("</h1>\n            ");
		if (registered) {
			jteOutput.writeContent("\n            <p class=\"notice\">");
			jteOutput.setContext("p", null);
			jteOutput.writeUserContent(messages.get("common.registered"));
			jteOutput.writeContent("</p>\n            ");
		}
		jteOutput.writeContent("\n            ");
		if (error) {
			jteOutput.writeContent("\n            <p class=\"notice error\">");
			jteOutput.setContext("p", null);
			jteOutput.writeUserContent(messages.get("login.error"));
			jteOutput.writeContent("</p>\n            ");
		}
		jteOutput.writeContent("\n            <form action=\"/login\" method=\"post\">\n            <input type=\"hidden\"");
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
		jteOutput.writeContent(">\n            <label for=\"username\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("login.username"));
		jteOutput.writeContent("</label><input id=\"username\" name=\"username\" autocomplete=\"username\" required>\n            <label for=\"password\">");
		jteOutput.setContext("label", null);
		jteOutput.writeUserContent(messages.get("login.password"));
		jteOutput.writeContent("</label><input id=\"password\" name=\"password\" type=\"password\" autocomplete=\"current-password\" required>\n            <p><button type=\"submit\">");
		jteOutput.setContext("button", null);
		jteOutput.writeUserContent(messages.get("login.submit"));
		jteOutput.writeContent("</button></p>\n            </form>\n            <div class=\"provider-list\">\n            ");
		for (String provider : providerIds) {
			jteOutput.writeContent("\n            <a href=\"/oauth2/authorization/");
			jteOutput.setContext("a", "href");
			jteOutput.writeUserContent(provider);
			jteOutput.setContext("a", null);
			jteOutput.writeContent("\">");
			jteOutput.setContext("a", null);
			jteOutput.writeUserContent(messages.get("login.continueWith").replace("{0}", messages.get("provider." + provider)));
			jteOutput.writeContent("</a>\n            ");
		}
		jteOutput.writeContent("\n            </div>\n            <p><a href=\"/register\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("login.register"));
		jteOutput.writeContent("</a></p>\n            <p><a href=\"?lang=en\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("common.english"));
		jteOutput.writeContent("</a> · <a href=\"?lang=km\">");
		jteOutput.setContext("a", null);
		jteOutput.writeUserContent(messages.get("common.khmer"));
		jteOutput.writeContent("</a></p>\n            </section>\n          </main>\n        <script>\n            (function() {\n                if (window.location.hash && window.location.hash.indexOf('tgAuthResult=') !== -1) {\n                    var hash = window.location.hash.substring(1);\n                    var params = new URLSearchParams(hash);\n                    var tgAuthResult = params.get('tgAuthResult');\n                    if (tgAuthResult) {\n                        window.location.replace('/login/telegram?tgAuthResult=' + encodeURIComponent(tgAuthResult));\n                    }\n                }\n            })();\n        </script>\n    </body>\n</html>\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		Map<String, String> messages = (Map<String, String>)params.get("messages");
		String localeTag = (String)params.get("localeTag");
		String csrfParameterName = (String)params.get("csrfParameterName");
		String csrfToken = (String)params.get("csrfToken");
		List<String> providerIds = (List<String>)params.get("providerIds");
		boolean error = (boolean)params.get("error");
		boolean registered = (boolean)params.get("registered");
		render(jteOutput, jteHtmlInterceptor, messages, localeTag, csrfParameterName, csrfToken, providerIds, error, registered);
	}
}
