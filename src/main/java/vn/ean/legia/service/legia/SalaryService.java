package vn.ean.legia.service.legia;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.ean.legia.model.legia.SalaryInMonth;
import vn.ean.legia.model.legia.Staff;
import vn.ean.legia.model.legia.StaticMoney;
import vn.ean.legia.model.legia.TimeSheet;
import vn.ean.legia.repository.legia.StaffRepository;
import vn.ean.legia.repository.legia.StaticMoneyRepository;
import vn.ean.legia.repository.legia.TimeSheetRepository;
import vn.ean.legia.util.Lib;

@Service("legiaSalaryService")
public class SalaryService {
    
    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Autowired
    private StaticMoneyRepository staticMoneyRepository;

    public Page<SalaryInMonth> list(String month, String search, Pageable pageable) {
        List<SalaryInMonth> results = new ArrayList<>();

        List<Staff> staffs = staffRepository.findAll();
        for (Staff staff : staffs) {
            results.add(salaryInMonth(staff, month));
        }

        if (pageable == null) {
            pageable = PageRequest.of(0, results.size());
        }

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), results.size());
        List<SalaryInMonth> pageContent = results.subList(start, end);

        return new PageImpl<>(pageContent, pageable, results.size());
    }

    private SalaryInMonth salaryInMonth(Staff staff, String month) {
        SalaryInMonth obj = new SalaryInMonth();
        obj.setStaff(staff);

        List<String> workedDates = new ArrayList<>();

        int worktime = 0;
        int overtime = 0;
        int bonusSal = 0;
        int subSal = 0;

        Map<String, Long> targetMonth = Lib.getMonthTimestamps(month);
        List<TimeSheet> shifts = timeSheetRepository.findByStaffAndStartGreaterThanAndEndLessThanAndEndGreaterThan(staff, targetMonth.get("start"), targetMonth.get("end"), 0L);
        List<StaticMoney> statics = staticMoneyRepository.findByStaffAndCreatedDateGreaterThanAndCreatedDateLessThan(staff, targetMonth.get("start"), targetMonth.get("end"));

        for (TimeSheet item : shifts) {
            String date = Lib.timestampConvert(item.getStart(), "dd/MM/YYYY");

            worktime += item.getWorktime();
            overtime += item.getOvertime();

            if (!workedDates.contains(date) && (item.getWorktime() > 0 || item.getOvertime() > 0)) {
                workedDates.add(date);
            }
        }

        for (StaticMoney item : statics) {
            int amount = item.getAmount();

            if (amount > 0) {
                bonusSal += amount;
            } else {
                subSal += amount;
            }
        }

        double worktimeHr = Math.floor((worktime / 3600.0) * 10) / 10.0;
        double overtimeHr = Math.floor((overtime / 3600.0) * 10) / 10.0;

        int worktimeSal = calcWorktimeSal(staff.getSalary(), worktimeHr);
        int overtimeSal = calcOvertimeSal(overtimeHr);

        obj.setWorkedDates(workedDates.size() + "/" + Math.round((targetMonth.get("end") - targetMonth.get("start") + 3600) / 60 / 60 / 24));
        obj.setWorktime(worktimeHr);
        obj.setOvertime(overtimeHr);
        obj.setWorktimeSal(worktimeSal);
        obj.setOvertimeSal(overtimeSal);
        obj.setBonusSal(bonusSal);
        obj.setSubSal(subSal);
        obj.setTotal(worktimeSal + overtimeSal + bonusSal + subSal);
        return obj;
    }

    public int calcWorktimeSal(int salary, double time) {
        return (int) Math.floor(salary * time / 234 / 1000) * 1000;
    }

    public int calcOvertimeSal(double time) {
        return (int) Math.floor(time * 20000 / 1000) * 1000;
    }

    public String export(String month) {
        String[] monthArgs = month.split("/");
        String monthTitle = String.format("t%sn%s", monthArgs[0], monthArgs[1]);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Lương " + monthTitle);
        sheet.setColumnWidth(0, 20 * 256);
        sheet.setColumnWidth(1, 15 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(7, 15 * 256);

        CellStyle borderStyle = workbook.createCellStyle();
        borderStyle.setBorderTop(BorderStyle.THIN);
        borderStyle.setBorderBottom(BorderStyle.THIN);
        borderStyle.setBorderRight(BorderStyle.THIN);
        borderStyle.setBorderLeft(BorderStyle.THIN);

        DataFormat format = workbook.createDataFormat();

        CellStyle numberStyle = workbook.createCellStyle();
        CellStyle numberStyle2 = workbook.createCellStyle();
        numberStyle.setDataFormat(format.getFormat("#,##0"));
        numberStyle2.setDataFormat(format.getFormat("#,##0.0"));

        numberStyle.setBorderTop(BorderStyle.THIN);
        numberStyle.setBorderBottom(BorderStyle.THIN);
        numberStyle.setBorderRight(BorderStyle.THIN);
        numberStyle.setBorderLeft(BorderStyle.THIN);

        numberStyle2.setBorderTop(BorderStyle.THIN);
        numberStyle2.setBorderBottom(BorderStyle.THIN);
        numberStyle2.setBorderRight(BorderStyle.THIN);
        numberStyle2.setBorderLeft(BorderStyle.THIN);

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Nhân viên");
        header.createCell(1).setCellValue("Giờ hành chính");
        header.createCell(2).setCellValue("Lương hành chính");
        header.createCell(3).setCellValue("Giờ tăng ca");
        header.createCell(4).setCellValue("Lương tăng ca");
        header.createCell(5).setCellValue("Cộng lương");
        header.createCell(6).setCellValue("Trừ lương");
        header.createCell(7).setCellValue("Tạm tính");

        for (int i = 0; i <= 7; i++) {
            header.getCell(i).setCellStyle(borderStyle);
        }

        Page<SalaryInMonth> list = list(month, "", null);
        List<SalaryInMonth> items = list.getContent();

        int i;
        for (i = 0; i < items.size(); i++) {
            SalaryInMonth item = items.get(i);

            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(item.getStaff().getName());
            row.createCell(1).setCellValue(item.getWorktime());
            row.createCell(2).setCellValue(item.getWorktimeSal());
            row.createCell(3).setCellValue(item.getOvertime());
            row.createCell(4).setCellValue(item.getOvertimeSal());
            row.createCell(5).setCellValue(item.getBonusSal());
            row.createCell(6).setCellValue(item.getSubSal());

            String formula = String.format("C%d+E%d+F%d+G%d", i + 2, i + 2, i + 2, i + 2);
            row.createCell(7).setCellFormula(formula);

            for (int j = 0; j <= 7; j++) {
                row.getCell(j).setCellStyle(numberStyle);
            }

            row.getCell(1).setCellStyle(numberStyle2);
            row.getCell(3).setCellStyle(numberStyle2);
        }

        Row timeRow = sheet.createRow(i + 1);
        timeRow.createCell(0).setCellValue(String.format("Lương %s", month));
        timeRow.getCell(0).setCellStyle(borderStyle);
        timeRow.createCell(1).setCellValue(Lib.timestampConvert(Lib.currentTimestamp(), "HH:mm dd/MM/YYYY"));
        timeRow.getCell(1).setCellStyle(borderStyle);

        String fileDir = "legia_bang_luong.xlsx";
        File file = new File(fileDir);

        if (file.exists()) {
            if (!file.delete()) {
                System.out.println("Could not delete existing file: " + file.getAbsolutePath());
            }
        }

        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
            workbook.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileDir;
    }
}
