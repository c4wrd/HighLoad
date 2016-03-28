package com.c4wrd.loadtester.data;

import com.c4wrd.loadtester.HighLoad;
import com.c4wrd.loadtester.configuration.HighLoadConfig;
import com.c4wrd.loadtester.configuration.TestType;
import com.c4wrd.loadtester.exceptions.HighLoadException;
import com.c4wrd.loadtester.request.RequestDetail;
import com.c4wrd.loadtester.util.AverageReducer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.apache.poi.ss.usermodel.CellStyle.*;

public class DataSaver {

    private AverageReducer reducer;
    private List<RequestDetail> requests;
    private HighLoadConfig config;
    private XSSFWorkbook workbook;
    private XSSFSheet summarySheet;
    private XSSFSheet dataSheet;

    public DataSaver(List<RequestDetail> requests, HighLoadConfig config) throws HighLoadException {

        if ( requests.size() == 0 ) {
            throw new HighLoadException("The results cannot be size zero, please run the test again or adjust your configuration!");
        }

        this.requests = requests;
        this.config = config;
        this.reducer = AverageReducer.Create(requests);
        this.workbook = new XSSFWorkbook();
        this.create();
    }

    private void create() {
        this.summarySheet = workbook.createSheet("Summary");
        this.dataSheet = workbook.createSheet("Request Data");

        this.createSummary();
        this.createData();
    }

    private void createSummary() {
        this.createSummaryHeader();
        this.createOverallAverageTable();
    }

    private void createOverallAverageTable() {
        String[] cols = new String[] { "Endpoint", "Average Response Time" };
        List<Object[]> averagePerUrl = reducer.AverageDistinct();

        XSSFRow row = summarySheet.createRow(3);
        row.createCell(0).setCellValue("Endpoint");
        row.createCell(1).setCellValue("Average Response Time");

        int rowIndex = 4;
        for (Object[] item : averagePerUrl) {
            Row endpointRow = summarySheet.createRow(rowIndex++);
            endpointRow.createCell(0).setCellValue((String)item[0]);
            endpointRow.createCell(1).setCellValue((Double)item[1]);
        }

    }

    private void createSummaryHeader() {
        Map<String, Object[]> rowInformation = new TreeMap<>();
        rowInformation.put("1", new Object[] {
                "TEST TYPE", "HOST", "NUMBER OF THREADS", "INTERVAL", "ENDPOINTS"
        });

        rowInformation.put("2", new Object[] {
                config.getTestType().toString(),
                config.getHost(),
                String.valueOf(config.getTestConfig().getNumThreads()),
                String.valueOf(config.getTestConfig().getInterval()),
                config.getEndpoints().toString()
        });

        int rowId = 0;
        for (String key : rowInformation.keySet()) {
            XSSFRow row = summarySheet.createRow(rowId++);
            Object[] data = rowInformation.get(key);
            int cellIndex = 0;
            for ( Object obj : data ) {
                Cell cell = row.createCell(cellIndex++);
                cell.setCellValue((String)obj);
            }
        }
    }

    private void createData() {

        Row headerRow = dataSheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(CellStyle.BORDER_DOUBLE);
        headerRow.setRowStyle(style);

        headerRow.createCell(0).setCellValue("Endpoint");
        headerRow.createCell(1).setCellValue("Query");
        headerRow.createCell(2).setCellValue("Response Time (ms)");
        headerRow.createCell(3).setCellValue("Requested At (ms)");

        Map<String, List<RequestDetail>> detailsByEndpoint = requests.stream()
                .collect(Collectors.groupingBy(RequestDetail::getEndpoint));

        int rowIndex = 1;

        for ( Map.Entry<String, List<RequestDetail>> endpointDetails : detailsByEndpoint.entrySet() ) {

            List<RequestDetail> sorted = endpointDetails.getValue()
                    .stream()
                    .sorted(Comparator.comparingLong(RequestDetail::getRequestTime))
                    .collect(Collectors.toList());

            for ( RequestDetail detail : sorted ) {
                XSSFRow row = dataSheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(detail.getEndpoint());
                row.createCell(1).setCellValue(detail.getQueryParameters());
                row.createCell(2).setCellValue(detail.getServerResponseTime());
                row.createCell(3).setCellValue(detail.getRequestTime());

            }

            rowIndex++;

        }

    }

    public void save(String filePath) throws IOException {
        File file = new File(filePath);
        FileOutputStream fIP = new FileOutputStream(file);
        this.workbook.write(fIP);
    }

}
