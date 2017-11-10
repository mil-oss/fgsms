
$("#accordion").accordion({
    autoHeight: false,
    navigation: true
});
$("#recordstype").buttonset();
$("#exporttype").buttonset();
$("#ScheduleType").buttonset();
$("#joiner").buttonset();
$("input:submit").button();
$("input:button").button();
$("#untiltime").resizable();
$("#fromtime").resizable();
$("#additionalusers").resizable();
function toggleNewReport() {
    if ($("#accordion").is(":visible")) {
        $("#accordion").hide();
    } else
        $("#accordion").show();
}

function showtab(item)
{
    $("#accordion").accordion({active: item});
}


$("#fromdatepicker").datepicker({dateformat: 'yyyy-mm-dd'}).datepicker("setDate", new Date());
function  toggleVisiblityLocal(obj)
{
    if (obj == false)
    {
        var x = document.getElementById('csv');
        x.style.display = "none";
        x = document.getElementById('html');
        x.style.display = "";
    }
    else
    {
        var x = document.getElementById('csv');
        x.style.display = "";
        x = document.getElementById('html');
        x.style.display = "none";
    }
}
function checkAll()
{
    var e = document.getElementsByTagName("input");
    if (e != null)
    {
        for (var i = 0; i < e.length; i++)
        {

            if (e[i].getAttribute("type") == "checkbox")
            {
                var x = null;
                x = e[i].getAttribute("name");
                if (x != undefined)
                {
                    x = x.toString();
                    if (StringStartsWith(x, "URL"))
                    {
                        e[i].removeAttribute("checked");
                        check(e[i]);
                    }
                }
            }

        }
    }
}

function isAuditExportSelected()
{
    var x = document.getElementById("labelAuditLogs");
    if (x.getAttribute("aria-pressed") == "true")
        return true;
    return false;
}

//false if no items are checked
function isAtLeastOneUrlSelected()
{
    var e = document.getElementsByTagName("input");
    if (e != null)
    {
        var found = false;
        for (var i = 0; i < e.length; i++)
        {

            if (e[i].getAttribute("type") == "checkbox")
            {
                var x = null;
                x = e[i].getAttribute("name");
                if (x != undefined)
                {
                    x = x.toString();
                    if (StringStartsWith(x, "URL"))
                    {
                        var attrib = e[i].getAttribute("checked");
                        if (attrib != null)
                        {
//alert (x + " is checked");
                            return true;
                        }
                        try {
                            if (e[i].checked)
                                return true;
                        }
                        catch (e1) {
                        }
                    }
                }
            }
        }
    }
    return false;
}


function check(obj)
{
    try {
        obj.checked = 'checked';
    } catch (e)
    {
    }
    try {
        obj.setAttribute('checked', 'checked');
    } catch (e) {
    }
    try {
        obj.attributes['checked'] = 'checked';
    } catch (e) {
    }
}

function StringStartsWith(data, input)
{
    return (data.substring(0, input.length) === input);
}
function uncheckAll()
{
    var e = document.getElementsByTagName("input");
    if (e != null)
    {
        for (var i = 0; i < e.length; i++)
        {

            if (e[i].getAttribute("type") == "checkbox")
            {
                var x = null;
                x = e[i].getAttribute("name");
                if (x != undefined)
                {
                    x = x.toString();
                    if (StringStartsWith(x, "URL"))
                    {
                        try {
                            e[i].checked = false;
                        } catch (s) {
                        }
                        try {
                            e[i].removeAttribute("checked");
                        } catch (s) {
                        }

//check(e[i]);
                    }
                }
            }

        }
    }
}

function isHtmlExport()
{
    var x = document.getElementById("labelHTML");
    if (x.getAttribute("aria-pressed") == "true")
        return true;
    return false;
}

//return true if there is an error
function checkTime()
{
    var startAtHour = $('input[name="startathour"]').val();
    var startAtMinute = $('input[name="startatminute"]').val();
    if (startAtHour > 24 || startAtHour < 0)
        return true;
    if (startAtMinute > 59 || startAtMinute < 0)
        return true;
    return false;
}

function correctStartOnDate() {
    var x = $("#fromdatepicker").val(); //09/06/2012
    //split the value into 3 parts by "/"
    console.log("date: " + x);
    var split = x.split("/");
    if (split.length != 3)
        return false;
    else {
        var month = parseInt(split[0], 10);
        var day = parseInt(split[1], 10);
        var year = split[2];
        var yearsplit = year.split("");
        var blah = yearsplit.length;
        if (1 > month || month > 12)
            return false;
        if (1 > day || day > 31)
            return false;
        if (blah != 4)
            return false;
    }
    return true;
    //if size is not 3 -> false

    //set the first to month, second to day, and third to year

    //if the month is less than 1 or greater than 12, false
    //more ifs.. 
    //year depends on current year. 


}

//return true if there is an error
function validateSchedule()
{
//if immediate is not checked && count of selected items is 0 return true
    if (correctStartOnDate()) {
        if (!isImmediateChecked())
        {
            var x = 0;
            if (weekly())
            {
                x += CountSelectedItems("dayoftheweekselection");
                if (x == 0)
                    return true;
            }
            if (monthly())
            {
                x += CountSelectedItems("dayofmonthselection");
                x += CountSelectedItems("monthselection");
                if (x < 2)
                    return true;
            }
            return checkTime();
        }
        return true;
        //check start on date
        //check start at time for validity

    }
    return true;
}
function weekly()
{
    var x = document.getElementById("labelweekly");
    if (x.getAttribute("aria-pressed") == "true")
        return true;
    return false;
}
function monthly()
{
    var x = document.getElementById("labelmonthly");
    if (x.getAttribute("aria-pressed") == "true")
        return true;
    return false;
}
function CountSelectedItems(elementname)
{
    var element = document.getElementById(elementname);
    if (element == undefined)
        return 0;
    if (element == null)
        return 0;
    var count = 0;
    if (element.options == null)
        return 0;
    if (element.options == undefined)
        return 0;
    if (element.options.length == null)
        return 0;
    if (element.options.length == undefined)
        return 0;
    for (i = 0; i < element.options.length; i++) {
        if (element.options[i].selected) {
//selectedArray[count] = element.options[i].value;
            count++;
        }
    }
    return count;
}
function isImmediateChecked()
{
    return ($('input[name="ScheduleType"]:checked').size() <= 0);
}

function isExportTypeSelected()
{
    var ok = false;
    var x = document.getElementById("labelHTML");
    if (x.getAttribute("aria-pressed") == "true")
        ok = true;
    x = document.getElementById("labelCSV");
    if (x.getAttribute("aria-pressed") == "true")
        ok = true;
    return ok;
}


function isAtLeastOneHtmlReportSelected()
{
    var e = document.getElementsByTagName("input");
    if (e != null)
    {
        for (var i = 0; i < e.length; i++)
        {

            if (e[i].getAttribute("type") == "checkbox")
            {
                var x = null;
                x = e[i].getAttribute("name");
                if (x != undefined)
                {
                    x = x.toString();
                    if (!StringStartsWith(x, "URL") && x != "runimmediate")
                    {
                        var attrib = e[i].getAttribute("checked");
                        if (attrib != null)
                        {
//  alert (x + " is checked");
                            return true;
                        }
                        try {
                            if (e[i].checked)
                                return true;
                        }
                        catch (e1) {
                        }

                    }
                }
            }

        }
    }
    return false;
}
function validate()
{

    var ok = true;
    var err = "";
    //confirm that one type of report is selected
    //date range start is greater than end
    //confirm that at least one service is selected
    var isexportselected = isExportTypeSelected();
    var exporthtml = isHtmlExport();
    var oneselect = isAtLeastOneUrlSelected();
    var htmlreportsselect = isAtLeastOneHtmlReportSelected();
    var auditexport = isAuditExportSelected();
    if (!isexportselected)
    {
        ok = false;
        err += "You must select a choice of CSV export or HTML report<br>";
    }
    else
    {

        if (exporthtml)
        {
            if (!oneselect)
            {
                ok = false;
                err += "When creating an HTML report, at least one service URL must be selected<br>";
            }
            if (!htmlreportsselect)
            {
                ok = false;
                err += "When creating an HTML report, at least one report type must be selected<br>";
            }
        }
        else
        {
//export CSV
            if (!auditexport && !oneselect)
            {
                ok = false;
                err += "When creating a CSV export, at least one service URL must be selected<br>";
            }
            else
            {
//export csv audit logs
//no special requirements
            }
            if (!CVXExportOptionSelected())
            {
                ok = false;
                err += "When creating a CSV export, you  must select an export type.<br>";
            }

        }
    }



//confirm that a time range is selected and is valid
    if (validateDurations())
    {
        ok = false;
        err += "At least one of the time periods (durations) are invalid, return to that section for details<br>";
    }



//confirm schedule is set and valid
    if (validateSchedule())
    {

        ok = false;
        err += "The schedule you defined is invalid, see that section for details<br>";
    }

    if (ok)
    {
        document.getElementById("errors").innerHTML = "Everythings OK, hang on....";
        //postBackReRender('addingJob', 'reporting/old/testing.jsp', 'mainpane'); 
        postBackReRender('addingJob', 'reporting/scheduledMainPost.jsp', 'mainpane');
        // postBack('addingJob', 'reporting/scheduledMainPost.jsp'); 
        //document.forms["form1"].submit();
        //     form.submit();/
    } else
    {
        document.getElementById("errors").innerHTML = err;
    }
}

//return true if there is an error
function validateDurations()
{

    var x = sendValidationRequest(document.getElementById('fromtime').value, 'fromtimeerror');
    var y = sendValidationRequest(document.getElementById('untiltime').value, 'untiltimeerror');
    return (x == false || y == false);
}


//sends a duration validation requests
//val = the users input
//elementToUpdate is an element on the page that will have the inner html text updated with the response
//returns true is its OK, otherwise false
//will block until a response comes back
function sendValidationRequest(val, elementToUpdate) {
    var xmlHttpReq = false;
    var XMLHttpRequestObject = false;
    // Mozilla/Safari
    if (window.XMLHttpRequest) {
        XMLHttpRequestObject = new XMLHttpRequest();
    }
// IE
    else if (window.ActiveXObject) {
//  self.xmlHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
        try {
            XMLHttpRequestObject = new ActiveXObject("Msxml2.XMLHTTP.6.0");
        }
        catch (e) {
        }
        try {
            XMLHttpRequestObject = new ActiveXObject("Msxml2.XMLHTTP.3.0");
        }
        catch (e) {
        }
        try {
            XMLHttpRequestObject = new ActiveXObject("Microsoft.XMLHTTP");
        }
        catch (e) {
        }
// XMLHttpRequestObject = new ActiveXObject("Microsoft.XMLHTTP");
    }
    XMLHttpRequestObject.open('GET', 'help/datevalidator.jsp?val=' + val, false);
    //XMLHttpRequestObject.timeout = 10000;
    XMLHttpRequestObject.send(null);
    //XMLHttpRequestObject.onreadystatechange = function() {
    //            if (XMLHttpRequestObject.readyState == 4) {
    if (XMLHttpRequestObject.status == 200)
    {
        document.getElementById(elementToUpdate).innerHTML = XMLHttpRequestObject.responseText;
        if (XMLHttpRequestObject.responseText == "Valid!")
            return true;
        else
            return false;
    }
    else {
        document.getElementById(elementToUpdate).innerHTML = "Unable to validate: " + XMLHttpRequestObject.responseText;
        return false;
    }
// else window.location = "index.jsp";

//        }
//}

}
function addDayOfWeek()
{
// alert("1");
    var current;
    var dayoftheweekselection = document.getElementById("dayoftheweekselection");
    if (dayoftheweekselection != null)
    {
        current = dayoftheweekselection.options[dayoftheweekselection.selectedIndex].value;
    }
    var listbox = document.getElementById("dayoftheweek");
    //     var x=document.getElementById("mySelect");
    var option = document.createElement("option");
    option.text = current;
    var exists = false;
    $('#dayoftheweek option').each(function ()
    {
        if (this.value == current)
        {
            exists = true;
        }
    });
    if (!exists)
    {
        try
        {
// for IE earlier than version 8
            listbox.add(option, x.options[null]);
        }
        catch (e)
        {
            listbox.add(option, null);
        }
    }
}
function removeDayOfWeek()
{
//    alert("2");
    var current;
    //var dayoftheweekselection=document.getElementById("dayoftheweekselection");
    var listbox = document.getElementById("dayoftheweek");
    if (dayoftheweekselection != null)
    {
        current = listbox.options[listbox.selectedIndex];
        if (current != null)
        {
            listbox.remove(listbox.selectedIndex);
        }
    }
}
function addHourOfDay()
{
//   alert("3");
    var current;
    var dayoftheweekselection = document.getElementById("hourofdayselection");
    if (dayoftheweekselection != null)
    {
        current = dayoftheweekselection.options[dayoftheweekselection.selectedIndex].value;
    }
    var listbox = document.getElementById("hourofday");
    var option = document.createElement("option");
    option.text = current;
    var exists = false;
    $('#hourofday option').each(function ()
    {
        if (this.value == current)
        {
            exists = true;
        }
    });
    if (!exists)
    {
        try
        {
// for IE earlier than version 8
            listbox.add(option, x.options[null]);
        }
        catch (e)
        {
            listbox.add(option, null);
        }
    }
}
function removeHourOfDay()
{
//     alert("4");
    var current;
    //var dayoftheweekselection=document.getElementById("dayoftheweekselection");
    var listbox = document.getElementById("hourofday");
    if (dayoftheweekselection != null)
    {
        current = listbox.options[listbox.selectedIndex];
        if (current != null)
        {
            listbox.remove(listbox.selectedIndex);
        }
    }
}
function addDayOfMonth()
{
//    alert("5");
    var current;
    var dayoftheweekselection = document.getElementById("dayofmonthselection");
    if (dayoftheweekselection != null)
    {
        current = dayoftheweekselection.options[dayoftheweekselection.selectedIndex].value;
    }
    var listbox = document.getElementById("dayofmonth");
    var option = document.createElement("option");
    option.text = current;
    var exists = false;
    $('#dayofmonth option').each(function ()
    {
        if (this.value == current)
        {
            exists = true;
        }
    });
    if (!exists)
    {
        try
        {
// for IE earlier than version 8
            listbox.add(option, x.options[null]);
        }
        catch (e)
        {
            listbox.add(option, null);
        }
    }
}
function removeDayOfMonth()
{
//     alert("6");
    var current;
    //var dayoftheweekselection=document.getElementById("dayoftheweekselection");
    var listbox = document.getElementById("dayofmonth");
    if (dayoftheweekselection != null)
    {
        current = listbox.options[listbox.selectedIndex];
        if (current != null)
        {
            listbox.remove(listbox.selectedIndex);
        }
    }
}