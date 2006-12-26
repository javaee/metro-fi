/*
 * Fast Infoset Round Trip Test software ("Software")
 *
 * Copyright, 2004-2005 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Software is licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations.
 *
 *    Sun supports and benefits from the global community of open source
 * developers, and thanks the community for its important contributions and
 * open standards-based technology, which Sun has adopted into many of its
 * products.
 *
 *    Please note that portions of Software may be provided with notices and
 * open source licenses from such communities and third parties that govern the
 * use of those portions, and any licenses granted hereunder do not alter any
 * rights and obligations you may have under such open source licenses,
 * however, the disclaimer of warranty and limitation of liability provisions
 * in this License will apply to all Software in this distribution.
 *
 *    You acknowledge that the Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any nuclear
 * facility.
 *
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 */

package com.sun.xml.fastinfoset.roundtriptests;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Alexey Stashok
 */
public class RoundTripTestExecutor {
    
    private File report;
    private RoundTripRtt test;
    
    public RoundTripTestExecutor(RoundTripRtt test, File report) {
        this.test = test;
        this.report = report;
    }
    
    public void processRtt(File srcFile) {
        boolean passed;
        try {
            System.out.println(test.getName() + ": " + srcFile.getAbsolutePath());
            passed = test.process(srcFile);
        } catch (Exception ex) {
            passed = false;
            System.err.println("Exception occured when processing file: " + srcFile.getAbsolutePath() + " test: " + test.getClass().getName());
            ex.printStackTrace();
        }
        String passedStr = passed ? "passed" : "failed";
        System.out.println(passedStr.toUpperCase());
        
        String reportFolder = report.getParent() != null ? report.getParent() : ".";
        String srcFolder = srcFile.getParent() != null ? srcFile.getParent() : ".";
        RoundTripReport.main(new String[] {reportFolder, report.getName(), srcFile.getName(), srcFolder, test.getName(), passedStr});
    }
    
    public void processFileOrFolder(File srcFile) {
        FileUtils.processFileOrDirectoryRecursivelly(srcFile, new FileFilter() {
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return !file.getName().equals(".") && !file.getName().equals("..");
                } else if (file.isFile()) {
                    return file.getName().endsWith(".xml");
                }
                return false;
            }}, new FileUtils.FileProcessorHandler() {
                public void handle(final File file) {
                    processRtt(file);
                }
            });
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: RoundTripTestExecutor <RoundTripRtt classname> <src file or directory> <report_filename>");
            System.exit(0);
        }
        
        new RoundTripTestExecutor((RoundTripRtt) Class.forName(args[0]).newInstance(), new File(args[2])).processFileOrFolder(new File(args[1]));
    }
}
