import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Main {
    private static ArrayList<Marks> marks = new ArrayList<>();
    private static boolean isFound;
    private final static String FILE_NAME = "/home/nd/Projects/Java/Java_XMLParser/cars.xmls";
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

        if (!Files.exists(Path.of(FILE_NAME))) {
            System.out.printf("Error reading file: file (%s) not exist\n", FILE_NAME);
            return;
        }

        String url = "https://www.drom.ru/cached_files/autoload/files/ref.xml";
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(false);
        f.setValidating(false);
        DocumentBuilder b = f.newDocumentBuilder();
        URLConnection urlConnection = new URL(url).openConnection();
        urlConnection.addRequestProperty("Accept", "application/xml");

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SearchingXMLHandler scHandler = new SearchingXMLHandler("Marks");

        XMLReader reader = factory.newSAXParser().getXMLReader();
        reader.setContentHandler(scHandler);

        InputSource source = new InputSource(FILE_NAME);
        source.setEncoding(StandardCharsets.UTF_8.displayName());
        reader.parse(source);

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

