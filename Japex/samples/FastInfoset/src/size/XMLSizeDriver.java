package size;
/*
 * Japex ver. 0.1 software ("Software")
 * 
 * Copyright, 2004-2005 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This Software is distributed under the following terms:
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, is permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistribution in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc., 'Java', 'Java'-based names,
 * nor the names of contributors may be used to endorse or promote products
 * derived from this Software without specific prior written permission.
 * 
 * The Software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS
 * SHALL NOT BE LIABLE FOR ANY DAMAGES OR LIABILITIES SUFFERED BY LICENSEE
 * AS A RESULT OF OR RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE
 * SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE
 * LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED
 * AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that the Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

import java.io.*;
import javax.xml.parsers.*;
import java.util.Properties;

import com.sun.japex.*;

public class XMLSizeDriver extends JapexDriverBase {
    
    protected String _xmlFile;
    protected byte[] _xmlFileByteArray;
    
    public XMLSizeDriver() {
    }

    public void initializeDriver() {
    }   
    
    public void prepare(TestCase testCase) {
        _xmlFile = testCase.getParam("xmlfile");
        if (_xmlFile == null) {
            throw new RuntimeException("xmlfile not specified");
        }
        
        try {
            FileInputStream fis = new FileInputStream(new File(_xmlFile));
            _xmlFileByteArray = com.sun.japex.Util.streamToByteArray(fis);
            fis.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void warmup(TestCase testCase) {
    }
    
    public void run(TestCase testCase) {
    }
    
    public void finish(TestCase testCase) {
        testCase.setDoubleParam(Constants.RESULT_VALUE, 
            _xmlFileByteArray.length / 1024.0);
    }
    
    public void terminateDriver() {
    }
}
