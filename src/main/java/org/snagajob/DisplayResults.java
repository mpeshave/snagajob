package org.snagajob;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Displays results in a tabular format for a questionnaire for a certain time period.
 * </p>
 * Run it as: DisplayResults <output_dir> <questionnaire_name> <from_date> [to_date]
 */
public class DisplayResults {

    public static void main(String[] args) {

        if (args.length < 3 || args.length > 4) {
            System.out.printf("Must have atleast 3 args: %s\n", args.length);
            System.out.println("Usage: DisplayResults <output_dir> <questionnaire_name> <from_date> [to_date]");
            System.exit(-1);
        }

        Store.setProcessedDir(args[0].trim());
        String questionnaire = args[1].trim();
        String fromDateStr = args[2].trim();
        String toDateStr = args.length == 4 ? args[3].trim() : null;
        Calendar cal = Calendar.getInstance();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date fromDate = null;
        Date toDate = null;
        try {
            fromDate = df.parse(fromDateStr);
        } catch (ParseException e) {
            System.out.printf("%s, must be 'yyyy-mm-dd'", e.getMessage());
            System.exit(-1);
        }

        if (toDateStr != null && !toDateStr.isEmpty()) {
            try {
                cal.setTime(df.parse(toDateStr));
                cal.add(Calendar.DATE, 1);
                toDate = cal.getTime();
            } catch (ParseException e) {
                System.out.printf("%s, must be 'yyyy-mm-dd'", e.getMessage());
                System.exit(-1);
            }
        }

        List<Date> dateList = new ArrayList<Date>();
        if (toDate == null) {
            dateList.add(fromDate);
        } else {
            if (toDate.before(fromDate)) {
                System.out.printf("toDate cant be before fromDate, toDate:%s  fromDate:%s",
                        df.format(toDate), df.format(fromDate));
                System.exit(-1);
            }
            cal.setTime(fromDate);
            while (cal.getTime().before(toDate)) {
                dateList.add(cal.getTime());
                cal.add(Calendar.DATE, 1);
            }
        }

        System.out.println(" Candidate Name\t\tQuest\t\tDate");
        for (Date date : dateList) {
            String dateStr = df.format(date);
            File[] clearedFiles = Store.getClearedForQuestionnaire(questionnaire, dateStr);
            if (clearedFiles != null) {
                for (File f : clearedFiles) {
                    System.out.printf(" %s\t\t%s\t\t%s\n", f.getName(), questionnaire, dateStr);
                }
            }
        }
    }
}
