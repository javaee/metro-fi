/*
 * FastInfosetSAXSizeDriver.java
 *
 * Created on October 28, 2005, 4:23 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.sun.japex.jdsl.xml.parsing.sax;

import java.io.File;

import com.sun.japex.Constants;
import com.sun.japex.TestCase;

/**
 *
 * @author sp106478
 */
public class FastInfosetSAXSizeDriver extends FastInfosetSAXDriver {
    
    public void finish(TestCase testCase) {
        super.finish(testCase);
        
        _inputStream.reset();
        testCase.setDoubleParam(Constants.RESULT_VALUE_X,
                                _inputStream.available() / 1024.0);
        getTestSuite().setParam(Constants.RESULT_UNIT_X, "kbs");
    }
  
}
