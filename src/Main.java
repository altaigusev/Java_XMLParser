import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Main {
    private static ArrayList<Marks> marks = new ArrayList<>();
    private static boolean isFound;
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

        String url = "https://www.drom.ru/cached_files/autoload/files/ref.xml";
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(false);
        f.setValidating(false);
        DocumentBuilder b = f.newDocumentBuilder();
        URLConnection urlConnection = new URL(url).openConnection();
        urlConnection.addRequestProperty("Accept", "application/xml");
        Document doc = b.parse(urlConnection.getInputStream());
        doc.getDocumentElement().normalize();
        System.out.println ("Root element: " +  doc.getDocumentElement().getNodeName());
        System.out.println("66:");


        SAXParserFactory factory = SAXParserFactory.newInstance();
        SearchingXMLHandler scHandler = new SearchingXMLHandler("Marks");
        SAXParser prs = factory.newSAXParser();
        prs.parse(url, scHandler);

        for (Marks mark : marks) {
            System.out.println(String.format("%s %s ", mark.getIdMark(), mark.getsMark()) );
        }
    }


    private static class SearchingXMLHandler extends DefaultHandler {
        private String element;
        private String idMark, sMark;
        private boolean isEntered;

        public SearchingXMLHandler(String element) {
            this.element = element;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            element = qName;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String info = new String(ch, start, length);
//            if (Character.UnicodeBlock.of(info.charAt(0)).equals(Character.UnicodeBlock.CYRILLIC)) info = "Cyrillic carname";
            if (Character.UnicodeBlock.of(info.charAt(0)).equals(Character.UnicodeBlock.CYRILLIC)) System.out.println("info = " + info);
            if(!info.isEmpty()) {
                if (element.equals("idMark")) idMark = info;
                if (element.equals("sMark")) sMark = info;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if( (idMark != null && !idMark.isEmpty()) && (sMark != null && !sMark.isEmpty())) {
//                if (Character.UnicodeBlock.of(info.charAt(0)).equals(Character.UnicodeBlock.CYRILLIC)) System.out.println();
                marks.add(new Marks(idMark, sMark));
                idMark = null;
                sMark = null;
            }
            if (qName.equals(element))
                isEntered = false;
        }
    }
}

