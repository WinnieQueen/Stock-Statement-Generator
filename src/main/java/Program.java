import java.io.*;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Program {
    public static void main(String[] args) {
        readJSON();
        writeToPDF();
    }

    public static void writeToHTML(String[] pieces, ArrayList<String[]> table) {
        //pieces = {acctNum, first name, last name, ssn, email, phone, balance}
        /*table =
        {
          { type
            symbol
          count
          price
          total
          },
          { type
          symbol
          count
          price
          total
          }
        }
        */
        try {

            File htmlDirectory = new File("htmlFiles");
            htmlDirectory.mkdir();

            OutputStream outputStream = new FileOutputStream("htmlFiles/" + pieces[0] + ".html");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);

            outputStreamWriter.write("<!DOCTYPE html><html><body>");

            //put date ran in html
            outputStreamWriter.write(String.format("<h2>Date Ran: %s </h2>", getTodaysDate()));

            //put holder's name at top
            outputStreamWriter.write(String.format("<h2>%s %s</h2>", pieces[1], pieces[2]));

            //put holder's info
            outputStreamWriter.write(String.format("<h3>-Holder Info- <br/><br/>SSN: %s <br/>Email: %s <br/>Phone: %s <br/>Beginning Balance: %s <br/><br/></h3>", pieces[3], pieces[4], pieces[5], pieces[6]));

            //set up table
            outputStreamWriter.write("<h3>-Share Info-</h3> <table> <tr><th>Type</th><th>Symbol</th><th>Count</th><th>Price</th><th>Total</th></tr>");

            //add all share info into the table
            for (String[] tablePiece : table) {
                outputStreamWriter.write(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", tablePiece[0], tablePiece[1], tablePiece[2], tablePiece[3], tablePiece[4]));
            }

            //end table
            outputStreamWriter.write("</table>");

            //finish html file
            outputStreamWriter.write("</body></html>");

            outputStreamWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getTodaysDate() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return String.format("%s %s, %s", new SimpleDateFormat("MMMM").format(calendar.getTime()), calendar.get(Calendar.DATE), calendar.get(Calendar.YEAR));
    }

    public static String writeToPDF() {
        File pdfDirectory = new File("pdfFiles");
        pdfDirectory.mkdir();

        File htmlFilesDir = new File("htmlFiles");
        File[] files = htmlFilesDir.listFiles();

        if (files != null) {
            for (File file : files) {
                String fileName = file.getName();
                String path = file.getAbsolutePath();
                Path p = Path.of(path);
                String number = fileName.substring(0, fileName.length() - 4);

                try (OutputStream os = new FileOutputStream("pdfFiles/" + number + "pdf")) {
                    PdfRendererBuilder builder = new PdfRendererBuilder();
                    builder.useFastMode();
                    builder.withUri(p.toUri().toString());
                    builder.toStream(os);
                    builder.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static void readJSON() {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("stock_transactions.by.account.holder.json")) {
            Object obj = jsonParser.parse(reader);

            JSONArray personList = (JSONArray) obj;

            personList.forEach(person -> parsePersonObject((JSONObject) person));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<ArrayList<String[]>> parsePersonObject(JSONObject person) {

        String[] pieces = getHolderInfo(person);

        JSONArray table = (JSONArray) person.get("stock_trades");

        ArrayList<String[]> tablePieces = getTablePieces(table);

        writeToHTML(pieces, tablePieces);

        ArrayList<String[]> s = new ArrayList<>();
        s.add(pieces);


        ArrayList<ArrayList<String[]>> finishedList = new ArrayList<>();
        finishedList.add(s);
        finishedList.add(tablePieces);

        return finishedList;
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<String[]> getTablePieces(JSONArray table) {
        ArrayList<String[]> tablePieces = new ArrayList<>();

        table.forEach(piece -> tablePieces.add(parseTableObject((JSONObject) piece)));

        return tablePieces;
    }

    public static String[] getHolderInfo(JSONObject person) {
        String[] info = new String[7];
        info[0] = person.get("account_number").toString();
        info[1] = person.get("first_name").toString();
        info[2] = person.get("ssn").toString();
        info[3] = person.get("last_name").toString();
        info[4] = person.get("email").toString();
        info[5] = person.get("phone").toString();
        info[6] = person.get("beginning_balance").toString();

        return info;
    }


    public static String[] parseTableObject(JSONObject tableObject) {
        double count = Double.parseDouble(tableObject.get("count_shares").toString());
        double price = Double.parseDouble(tableObject.get("price_per_share").toString().substring(1));
        double total = count * price;

        return new String[]{tableObject.get("type").toString(), tableObject.get("stock_symbol").toString(), tableObject.get("count_shares").toString(), tableObject.get("price_per_share").toString(), "$" + String.format("%.2f", total)};
    }
}
