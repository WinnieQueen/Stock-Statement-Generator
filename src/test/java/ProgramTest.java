import com.openhtmltopdf.util.IOUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ProgramTest {

    @Test
    void getTodaysDate() {
        String expected = "April 11, 2020";
        String actual = Program.getTodaysDate();

        assertEquals(expected, actual);
    }

    @Test
    void writeToHTML() {
        String[] pieces = {"1", "Margalo", "Trobey", "418-99-5984", "mtrobey0@cocolog-nifty.com", "418-99-5984", "$7680675.39"};
        ArrayList<String[]> table = new ArrayList<>();
        String[] trade1 = {"Sell", "CTBB", "7866", "$359.40", "$2827040.40"};
        String[] trade2 = {"Buy", "CSTR", "5912", "$307.45", "$1817644.40"};
        table.add(trade1);
        table.add(trade2);

        Program.writeToHTML(pieces, table);

        String expected = "<!DOCTYPE html><html><body><h2>Date Ran: April 11, 2020 </h2><h2>Margalo Trobey</h2><h3>-Holder Info- <br/><br/>SSN: 418-99-5984 <br/>Email: mtrobey0@cocolog-nifty.com <br/>Phone: 418-99-5984 <br/>Beginning Balance: $7680675.39 <br/><br/></h3><h3>-Share Info-</h3> <table> <tr><th>Type</th><th>Symbol</th><th>Count</th><th>Price</th><th>Total</th></tr><tr><td>Sell</td><td>CTBB</td><td>7866</td><td>$359.40</td><td>$2827040.40</td></tr><tr><td>Buy</td><td>CSTR</td><td>5912</td><td>$307.45</td><td>$1817644.40</td></tr></table></body></html>";

        File htmlFilesDir = new File("htmlFiles");
        File[] files = htmlFilesDir.listFiles();

        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader("htmlFiles\\1.html"));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
        }
        String actual = contentBuilder.toString();

        assertEquals(expected, actual);
    }

    @Test
    void writeToPDF() {
        File htmlFilesDir = new File("htmlFiles");

        Program.writeToPDF();

        String expected = "Date Ran: April 11, 2020\r\n" +
                "Margalo Trobey\r\n" +
                "-Holder Info- \r\n" +
                "SSN: 418-99-5984 \r\n" +
                "Email: mtrobey0@cocolog-nifty.com \r\n" +
                "Phone: 418-99-5984 \r\n" +
                "Beginning Balance: $7680675.39 \r\n" +
                "-Share Info-\r\n" +
                "Type Symbol Count Price Total\r\n" +
                "Sell CTBB 7866 $359.40 $2827040.40\r\n" +
                "Buy CSTR 5912 $307.45 $1817644.40\r\n";

        File file2 = new File("pdfFiles\\testFile.pdf");

        String actual = "";
        try {
            PDDocument document = PDDocument.load(file2);
            //Instantiate PDFTextStripper class
            PDFTextStripper pdfStripper = null;
            pdfStripper = new PDFTextStripper();
            //Retrieving text from PDF document
            actual = pdfStripper.getText(document);
            //Closing the document
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expected, actual);
    }

    @Test
    void readJSON() {
        Program.readJSON();
        File htmlDir = new File("htmlFiles");
        File[] files = htmlDir.listFiles();

        int expected = 301;
        int actual = files.length;

        assertEquals(expected, actual);
    }

    @Test
    void parsePersonObject() {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("account_number", 1);
        jsonObject.put("ssn", "418-99-5984");
        jsonObject.put("first_name", "Margalo");
        jsonObject.put("last_name", "Trobey");
        jsonObject.put("email", "mtrobey0@cocolog-nifty.com");
        jsonObject.put("phone", "654-711-3196");
        jsonObject.put("beginning_balance", "7680675.39");

        JSONArray table = new JSONArray();

        JSONObject array1 = new JSONObject();
        array1.put("type", "Sell");
        array1.put("stock_symbol", "CTBB");
        array1.put("count_shares", 7866);
        array1.put("price_per_share", "$359.40");
        JSONObject array2 = new JSONObject();
        array2.put("type", "Buy");
        array2.put("stock_symbol", "CSTR");
        array2.put("count_shares", 5912);
        array2.put("price_per_share", "$307.45");

        table.add(0, array1);
        table.add(1, array2);

        jsonObject.put("stock_trades", table);





        ArrayList<ArrayList<String[]>> grabbed = Program.parsePersonObject(jsonObject);

        StringBuilder sb = new StringBuilder();
        for (ArrayList<String[]> list : grabbed) {
            for (String[] stringArr : list) {
                for (String string : stringArr) {
                    sb.append(string + "\n");
                }
            }
        }

        String expected = "1\n" +
                "Margalo\n" +
                "418-99-5984\n" +
                "Trobey\n" +
                "mtrobey0@cocolog-nifty.com\n" +
                "654-711-3196\n" +
                "7680675.39\n" +
                "Sell\n" +
                "CTBB\n" +
                "7866\n" +
                "$359.40\n" +
                "$2827040.40\n" +
                "Buy\n" +
                "CSTR\n" +
                "5912\n" +
                "$307.45\n" +
                "$1817644.40\n";

        String actual = sb.toString();

        assertEquals(expected, actual);

    }

    @Test
    void getTablePieces() {
        JSONArray table = new JSONArray();

        JSONObject array1 = new JSONObject();
        array1.put("type", "Sell");
        array1.put("stock_symbol", "CTBB");
        array1.put("count_shares", 7866);
        array1.put("price_per_share", "$359.40");
        JSONObject array2 = new JSONObject();
        array2.put("type", "Buy");
        array2.put("stock_symbol", "CSTR");
        array2.put("count_shares", 5912);
        array2.put("price_per_share", "$307.45");

        table.add(0, array1);
        table.add(1, array2);

        ArrayList<String[]> expected = new ArrayList<>();
        String[] stringArr1 = {"Sell", "CTBB", "7866", "$359.40", "$2827040.40"};
        String[] stringArr2 = {"Buy", "CSTR", "5912", "$307.45", "$1817644.40"};
        expected.add(stringArr1);
        expected.add(stringArr2);

        ArrayList<String[]> actual = Program.getTablePieces(table);

        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size() && i < actual.size(); i++) {
            String[] e = expected.get(i);
            String[] a = actual.get(i);
            assertEquals(e.length, a.length);
            for (int j = 0; j < e.length && j < a.length; j++) {
                assertEquals(e[j], a[j]);
            }
        }

    }

    @Test
    void getHolderInfo() {
        JSONObject personInfo = new JSONObject();
        personInfo.put("account_number", 1);
        personInfo.put("first_name", "Ash");
        personInfo.put("ssn", "555-55-5555");
        personInfo.put("last_name", "Hilliard");
        personInfo.put("email", "ashilliard@student.neumont.edu");
        personInfo.put("phone", "435-749-0375");
        personInfo.put("beginning_balance", "$120.30");

        String[] expected = {"1", "Ash", "555-55-5555", "Hilliard", "ashilliard@student.neumont.edu", "435-749-0375", "$120.30"};
        String[] actual = Program.getHolderInfo(personInfo);

        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length && i < actual.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    void parseTableObject() {
        JSONObject tableObject = new JSONObject();
        tableObject.put("type", "Sell");
        tableObject.put("stock_symbol", "CTBB");
        tableObject.put("count_shares", 7866);
        tableObject.put("price_per_share", "$359.40");

        String[] expected = {"Sell", "CTBB", "7866", "$359.40", "$2827040.40"};
        String[] actual = Program.parseTableObject(tableObject);

        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length && i < actual.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

}