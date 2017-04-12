package biz.netcentric.Slightly.renderer;

import biz.netcentric.Slightly.context.PersonsContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class renders a Person Element into a Html template.
 */
public class PersonRenderer {
    private final PersonsContext context;
    private Document incomingDocument;
    private Document outgoingDocument = new Document("<!DOCTYPE html>");

    public PersonRenderer() {
        this.context = new PersonsContext();
        outgoingDocument = Jsoup.parse("<!DOCTYPE html><html> <head></head> <body></body></html>");
    }

    public void prepareParser(File htmlBase) throws IOException {
        incomingDocument = Jsoup.parse(htmlBase, "UTF-8");
    }

    public void parse() {
        // Get all documentElements
        Elements headElements = incomingDocument.head().getAllElements();
        headElements.remove(0); //removing the core head element
        Elements bodyElements = incomingDocument.body().getAllElements();
        bodyElements.remove(0); //removing the core Body element

        // Iterate Thru the head Elements
        resolveElements(headElements, outgoingDocument.head());
        // Iterate thru the body elements
       resolveElements(bodyElements, outgoingDocument.body());
        // Iterate thru each one of them
    }

    private void resolveElements(Elements elements, Element core) {
        for (Element element : elements) {
            // RESOLVE DATA-IF
            final String elementText = element.toString().toLowerCase();
            if (elementText.contains("data-if")) {
                // obtain the value from the Javascript context
                // TODO: If the value is invalid (not present in Context), do not change the value to render
                if (isDataIFTrue(element)) {
                    // If true, remove the data-if attribute and save to display
                } else {
                    // if false, do not add the value to the outgoing Document
                    continue; //Do not add the element as the data-if is false.
                }
            }

            Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
            Matcher dollarExpression = pattern.matcher(element.toString());

            // RESOLVE server/javascript tag
            // execute the Javascript in the PersonsContext
            if (elementText.contains("server/javascript")) {
                resolveJavascriptCode(element);
            }
            //RESOLVE DATA-FOR
            // Identify if the element has a data-for attribute
            else if (elementText.contains("data-for")) {
                resolveForExpression(element, core);
            } else if (dollarExpression.find()) {
                //RESOLVE $ Expression
                resolvePlaceholderExpression(element, core);
            } else {
                //Element does not contain any Magic inside, just return
                core.appendChild(element);
            }
        }
    }

    private void resolveForExpression(Element element, Element core) {
        final Attribute first = element.attributes()
                .asList()
                .stream()
                .filter(attribute -> attribute.getKey().contains("data-for"))
                .findFirst().get();
        // Extract the variable name to be used
        String variableName = StringUtils.remove(first.getKey(), "data-for-");
        String value = first.getValue();

        // Look for the parameter, find if the element exists in the context
        // get the list of elements
        final Object list = this.getContext().findParameter(value);

        //We don't need this attrib anymore
        element.attributes().remove(first.getKey());
        // for each element -> remove the data-for out of the element

        List<String> data = null;
        if (list instanceof List<?>) {
            data = (List<String>) list;
        }

        for (String child : data) {
            // replace the ${} variable with the value coming in the context
            Map values = new HashMap<>();
            values.put(variableName, child);
            StrSubstitutor sub = new StrSubstitutor(values);
            final String replacedString = sub.replace(element.toString());
            core.append(replacedString);
        }
        // TODO: If the parameter contains 0 elements, do not render anything
        // TODO: If the parameter does not exist in the context, do not render anything
    }

    /**
     * If the DataIF attribute exists and is true, this method removes the attribute out of the list of elements and returns true
     * If it is false, returns false;
     **/

    private boolean isDataIFTrue(Element element) {
        boolean result = false;
        final Attributes attributes = element.attributes();
        for (Attribute attribute : attributes) {
            if (attribute.toString().contains("data-if")) {
                final String value = attribute.getValue();
                final Object isReal = context.findParameter(value);
                Boolean canWeDisplay = new Boolean(isReal.toString());
                result = canWeDisplay;
                if (canWeDisplay) {
                    element.removeAttr(attribute.getKey());
                }
                break;
            }
        }
        return result;
    }

    private void resolvePlaceholderExpression(Element element, Element core) {
        // identify if the element has a '${' component
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}");
        Matcher matcher = pattern.matcher(element.text());

        // obtain the variable value out of the context
        matcher.find();
        String placeHolder = matcher.group(1);
        final Object parameter = context.findParameter(placeHolder);
        String value = (String) parameter;
        // replace the ${} value with the value found in the context
        // if not found, keep the ${} name
        Map values = new HashMap<>();
        values.put(placeHolder, value);
        StrSubstitutor sub = new StrSubstitutor(values);
        final String replacedString = sub.replace(element.toString());
        core.append(replacedString);
    }

    private void resolveJavascriptCode(Element element) {
        final String javascriptCode = element.html();
        try {
            this.getContext().getCx().evaluateString(this.getContext().getScope(), javascriptCode, "javascriptCode", 1, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //DO NOT ADD THE ELEMENT TO THE RESULT
    }

    public String getRenderedFile() {
        return outgoingDocument.html();
    }

    public PersonsContext getContext() {
        return context;
    }
}
