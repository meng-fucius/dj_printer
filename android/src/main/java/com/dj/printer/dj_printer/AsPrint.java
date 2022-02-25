package com.dj.printer.dj_printer;

import android.graphics.Typeface;

import com.sewoo.jpos.command.CPCLConst;
import com.sewoo.jpos.printer.CPCLPrinter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class AsPrint {

    private CPCLPrinter cpclPrinter;

    public AsPrint() {
        System.out.println("cpcl初始化");
//        cpclPrinter = new CPCLPrinter();    //Default = English.
        //cpclPrinter = new CPCLPrinter("EUC-KR"); // Korean.
        cpclPrinter = new CPCLPrinter("GB2312"); //Chinese.
        cpclPrinter.setMeasure(CPCLConst.LK_CPCL_MILLI);
    }

    public void printAsCode(String code, String channel, String country, String countStr, int offset, boolean hasPlan) throws IOException {
        if (hasPlan) {
            PrintPlan(code, channel, country, countStr, offset);
        } else {
            PrintNoPlan(code, countStr, offset);
        }
    }

    private void PrintPlan(String code, String channel, String country, String countStr, int offset) throws IOException {
        //2-inch
        cpclPrinter.setForm(0, 203, 203, 500, 575, 0, 1);
        cpclPrinter.setMedia(CPCLConst.LK_CPCL_LABEL);
        cpclPrinter.setJustification(CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.setCPCLBarcode(0, 0, 0);
        cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_128, 1, CPCLConst.LK_CPCL_BCS_4RATIO, 130, 0, 26, code, 0);
        cpclPrinter.printCPCLText(0, 0, 3, 0, 170, code, 0);
        cpclPrinter.printLine(0, 220, 535, 220, 3);
        cpclPrinter.setJustification(CPCLConst.LK_CPCL_LEFT);
        cpclPrinter.printAndroidFont(40, 240, Typeface.DEFAULT_BOLD, true, "渠道名称:", 575, 30);
        cpclPrinter.printAndroidFont(170, 240, Typeface.DEFAULT, channel, 575, 30);
        cpclPrinter.printAndroidFont(40, 300, Typeface.DEFAULT, "目的地：" + country, 575, 30);
        cpclPrinter.printAndroidFont(400 - offset, 300, Typeface.DEFAULT_BOLD, true, "件数:", 575, 30);
        cpclPrinter.printAndroidFont(480 - offset, 300, Typeface.DEFAULT, countStr, 575, 30);
        cpclPrinter.printForm();
    }

    private void PrintNoPlan(String code, String countStr, int offset) throws IOException {
        //2-inch
        cpclPrinter.setForm(0, 203, 203, 500, 575, 0, 1);
        cpclPrinter.setMedia(CPCLConst.LK_CPCL_LABEL);
        cpclPrinter.setJustification(CPCLConst.LK_CPCL_CENTER);
        cpclPrinter.setCPCLBarcode(0, 0, 0);
        cpclPrinter.printCPCLBarcode(CPCLConst.LK_CPCL_0_ROTATION, CPCLConst.LK_CPCL_BCS_128, 1, CPCLConst.LK_CPCL_BCS_4RATIO, 130, 0, 26, code, 0);
        cpclPrinter.printCPCLText(0, 0, 3, 0, 170, code, 0);
        cpclPrinter.printLine(0, 220, 535, 220, 3);
        cpclPrinter.setJustification(CPCLConst.LK_CPCL_LEFT);
        cpclPrinter.printAndroidFont(40, 240, Typeface.DEFAULT_BOLD, true, "未建计划", 575, 50);
        cpclPrinter.printAndroidFont(400, 240, Typeface.DEFAULT_BOLD, true, "件数:", 575, 50);
        cpclPrinter.printAndroidFont(400 - offset, 300, Typeface.DEFAULT, countStr, 575, 30);
        cpclPrinter.printForm();
    }
    public int Get_Status() {
        return  cpclPrinter.status();
    }
}
