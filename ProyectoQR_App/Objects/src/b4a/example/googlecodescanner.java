package b4a.example;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.B4AClass;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.debug.*;

public class googlecodescanner extends B4AClass.ImplB4AClass implements BA.SubDelegator{
    private static java.util.HashMap<String, java.lang.reflect.Method> htSubs;
    private void innerInitialize(BA _ba) throws Exception {
        if (ba == null) {
            ba = new BA(_ba, this, htSubs, "b4a.example.googlecodescanner");
            if (htSubs == null) {
                ba.loadHtSubs(this.getClass());
                htSubs = ba.htSubs;
            }
            
        }
        if (BA.isShellModeRuntimeCheck(ba)) 
			   this.getClass().getMethod("_class_globals", b4a.example.googlecodescanner.class).invoke(this, new Object[] {null});
        else
            ba.raiseEvent2(null, true, "class_globals", false);
    }

 public anywheresoftware.b4a.keywords.Common __c = null;
public int _format_all_formats = 0;
public int _format_aztec = 0;
public int _format_codabar = 0;
public int _format_code_128 = 0;
public int _format_code_39 = 0;
public int _format_code_93 = 0;
public int _format_data_matrix = 0;
public int _format_ean_13 = 0;
public int _format_ean_8 = 0;
public int _format_itf = 0;
public int _format_pdf417 = 0;
public int _format_qr_code = 0;
public int _format_upc_a = 0;
public int _format_upc_e = 0;
public b4a.example.main _main = null;
public b4a.example.starter _starter = null;
public b4a.example.menuprincipal _menuprincipal = null;
public b4a.example.reportdialog _reportdialog = null;
public b4a.example.login _login = null;
public b4a.example.menuprincipal_maquilas _menuprincipal_maquilas = null;
public b4a.example.reportsbyarea _reportsbyarea = null;
public b4a.example.reportdetail _reportdetail = null;
public b4a.example.httputils2service _httputils2service = null;
public static class _scannerresult{
public boolean IsInitialized;
public boolean Success;
public String Value;
public anywheresoftware.b4j.object.JavaObject Barcode;
public void Initialize() {
IsInitialized = true;
Success = false;
Value = "";
Barcode = new anywheresoftware.b4j.object.JavaObject();
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public String  _class_globals() throws Exception{
 //BA.debugLineNum = 2;BA.debugLine="Sub Class_Globals";
 //BA.debugLineNum = 3;BA.debugLine="Public Const FORMAT_ALL_FORMATS = 0, FORMAT_AZTEC";
_format_all_formats = (int) (0);
_format_aztec = (int) (4096);
_format_codabar = (int) (8);
_format_code_128 = (int) (1);
_format_code_39 = (int) (2);
_format_code_93 = (int) (4);
 //BA.debugLineNum = 4;BA.debugLine="Public Const FORMAT_DATA_MATRIX = 16, FORMAT_EAN_";
_format_data_matrix = (int) (16);
_format_ean_13 = (int) (32);
_format_ean_8 = (int) (64);
_format_itf = (int) (128);
_format_pdf417 = (int) (2048);
_format_qr_code = (int) (256);
 //BA.debugLineNum = 5;BA.debugLine="Public Const FORMAT_UPC_A = 512, FORMAT_UPC_E = 1";
_format_upc_a = (int) (512);
_format_upc_e = (int) (1024);
 //BA.debugLineNum = 6;BA.debugLine="Type ScannerResult (Success As Boolean, Value As";
;
 //BA.debugLineNum = 7;BA.debugLine="End Sub";
return "";
}
public String  _initialize(anywheresoftware.b4a.BA _ba) throws Exception{
innerInitialize(_ba);
 //BA.debugLineNum = 10;BA.debugLine="Public Sub Initialize";
 //BA.debugLineNum = 12;BA.debugLine="End Sub";
return "";
}
public anywheresoftware.b4a.keywords.Common.ResumableSubWrapper  _scan(anywheresoftware.b4a.objects.collections.List _formats) throws Exception{
ResumableSub_Scan rsub = new ResumableSub_Scan(this,_formats);
rsub.resume(ba, null);
return (anywheresoftware.b4a.keywords.Common.ResumableSubWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.keywords.Common.ResumableSubWrapper(), rsub);
}
public static class ResumableSub_Scan extends BA.ResumableSub {
public ResumableSub_Scan(b4a.example.googlecodescanner parent,anywheresoftware.b4a.objects.collections.List _formats) {
this.parent = parent;
this._formats = _formats;
}
b4a.example.googlecodescanner parent;
anywheresoftware.b4a.objects.collections.List _formats;
anywheresoftware.b4j.object.JavaObject _builder = null;
int[] _f = null;
int _i = 0;
anywheresoftware.b4j.object.JavaObject _options = null;
anywheresoftware.b4j.object.JavaObject _scanning = null;
anywheresoftware.b4j.object.JavaObject _ctxt = null;
anywheresoftware.b4j.object.JavaObject _scanner = null;
anywheresoftware.b4j.object.JavaObject _o = null;
b4a.example.googlecodescanner._scannerresult _res = null;
int step4;
int limit4;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
{
parent.__c.ReturnFromResumableSub(this,null);return;}
case 0:
//C
this.state = 1;
 //BA.debugLineNum = 15;BA.debugLine="Dim builder As JavaObject";
_builder = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 16;BA.debugLine="builder.InitializeNewInstance(\"com/google/mlkit/v";
_builder.InitializeNewInstance("com/google/mlkit/vision/codescanner/GmsBarcodeScannerOptions.Builder".replace("/","."),(Object[])(parent.__c.Null));
 //BA.debugLineNum = 17;BA.debugLine="Dim f(Formats.Size - 1) As Int";
_f = new int[(int) (_formats.getSize()-1)];
;
 //BA.debugLineNum = 18;BA.debugLine="For i = 1 To Formats.Size - 1";
if (true) break;

case 1:
//for
this.state = 4;
step4 = 1;
limit4 = (int) (_formats.getSize()-1);
_i = (int) (1) ;
this.state = 13;
if (true) break;

case 13:
//C
this.state = 4;
if ((step4 > 0 && _i <= limit4) || (step4 < 0 && _i >= limit4)) this.state = 3;
if (true) break;

case 14:
//C
this.state = 13;
_i = ((int)(0 + _i + step4)) ;
if (true) break;

case 3:
//C
this.state = 14;
 //BA.debugLineNum = 19;BA.debugLine="f(i - 1) = Formats.Get(i)";
_f[(int) (_i-1)] = (int)(BA.ObjectToNumber(_formats.Get(_i)));
 if (true) break;
if (true) break;

case 4:
//C
this.state = 5;
;
 //BA.debugLineNum = 21;BA.debugLine="builder.RunMethod(\"setBarcodeFormats\", Array(Form";
_builder.RunMethod("setBarcodeFormats",new Object[]{_formats.Get((int) (0)),(Object)(_f)});
 //BA.debugLineNum = 23;BA.debugLine="Dim options As JavaObject = builder.RunMethod(\"bu";
_options = new anywheresoftware.b4j.object.JavaObject();
_options = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_builder.RunMethod("build",(Object[])(parent.__c.Null))));
 //BA.debugLineNum = 24;BA.debugLine="Dim scanning As JavaObject";
_scanning = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 25;BA.debugLine="Dim ctxt As JavaObject";
_ctxt = new anywheresoftware.b4j.object.JavaObject();
 //BA.debugLineNum = 26;BA.debugLine="ctxt.InitializeContext";
_ctxt.InitializeContext(ba);
 //BA.debugLineNum = 27;BA.debugLine="Dim scanner As JavaObject = scanning.InitializeSt";
_scanner = new anywheresoftware.b4j.object.JavaObject();
_scanner = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_scanning.InitializeStatic("com/google/mlkit/vision/codescanner/GmsBarcodeScanning".replace("/",".")).RunMethod("getClient",new Object[]{(Object)(_ctxt.getObject()),(Object)(_options.getObject())})));
 //BA.debugLineNum = 28;BA.debugLine="Dim o As JavaObject = scanner.RunMethod(\"startSca";
_o = new anywheresoftware.b4j.object.JavaObject();
_o = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_scanner.RunMethod("startScan",(Object[])(parent.__c.Null))));
 //BA.debugLineNum = 29;BA.debugLine="Do While o.RunMethod(\"isComplete\", Null).As(Boole";
if (true) break;

case 5:
//do while
this.state = 8;
while ((BA.ObjectToBoolean(_o.RunMethod("isComplete",(Object[])(parent.__c.Null))))==parent.__c.False) {
this.state = 7;
if (true) break;
}
if (true) break;

case 7:
//C
this.state = 5;
 //BA.debugLineNum = 30;BA.debugLine="Sleep(50)";
parent.__c.Sleep(parent.getActivityBA(),this,(int) (50));
this.state = 15;
return;
case 15:
//C
this.state = 5;
;
 if (true) break;

case 8:
//C
this.state = 9;
;
 //BA.debugLineNum = 32;BA.debugLine="Dim res As ScannerResult";
_res = new b4a.example.googlecodescanner._scannerresult();
 //BA.debugLineNum = 33;BA.debugLine="res.Initialize";
_res.Initialize();
 //BA.debugLineNum = 34;BA.debugLine="If o.RunMethod(\"isSuccessful\", Null) Then";
if (true) break;

case 9:
//if
this.state = 12;
if (BA.ObjectToBoolean(_o.RunMethod("isSuccessful",(Object[])(parent.__c.Null)))) { 
this.state = 11;
}if (true) break;

case 11:
//C
this.state = 12;
 //BA.debugLineNum = 35;BA.debugLine="res.Success = True";
_res.Success /*boolean*/  = parent.__c.True;
 //BA.debugLineNum = 36;BA.debugLine="res.Barcode = o.RunMethod(\"getResult\", Null)";
_res.Barcode /*anywheresoftware.b4j.object.JavaObject*/  = (anywheresoftware.b4j.object.JavaObject) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4j.object.JavaObject(), (java.lang.Object)(_o.RunMethod("getResult",(Object[])(parent.__c.Null))));
 //BA.debugLineNum = 37;BA.debugLine="res.Value = res.Barcode.RunMethod(\"getRawValue\",";
_res.Value /*String*/  = BA.ObjectToString(_res.Barcode /*anywheresoftware.b4j.object.JavaObject*/ .RunMethod("getRawValue",(Object[])(parent.__c.Null)));
 if (true) break;

case 12:
//C
this.state = -1;
;
 //BA.debugLineNum = 39;BA.debugLine="Return res";
if (true) {
parent.__c.ReturnFromResumableSub(this,(Object)(_res));return;};
 //BA.debugLineNum = 40;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public Object callSub(String sub, Object sender, Object[] args) throws Exception {
BA.senderHolder.set(sender);
return BA.SubDelegator.SubNotFound;
}
}
