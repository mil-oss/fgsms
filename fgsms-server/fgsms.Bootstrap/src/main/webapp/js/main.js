
//used to keep track of the current location for generating direct links
var currentpage = 'home.jsp';

function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}
function StringNthIndexOf(count, source, findvalue)
{
    if (source == null)
        return -1;
    var counter = 0;
    //start at 0
    for (i = 0; i < source.length; i++)
    {
        if (source.charAt(i) == findvalue)
            counter++;
        if (counter == count)
            return i;
    }
    return -1;

}



/**
 *used on data export page
 */
function goDataExport() {
    var data = $("#form1").serialize();
    window.open("reporting/dataExportPostback.jsp?" + data + "&return=reporting/dataExport.jsp", "_newtab");
}


/**
 *used on report generation page
 */
function goReportGen() {
    var data = $("#form1").serialize();
    window.open("reporting/reportGeneratorPostback.jsp?" + data + "&return=reporting/reportGenerator.jsp", "_newtab");
}

function ShowLinkGenerator()
{
    $('#directLinkModal').modal();
    var browserLocation = window.location.toString();
    //this should be something like http://localhost:port/FGSMSBootstrap/index.jsp


    if (!endsWith(browserLocation, 'index.jsp'))
    {
        var idx = StringNthIndexOf(4, browserLocation, ('/'));
        if (idx == -1)
            browserLocation += 'index.jsp';
        else
        {
            browserLocation = browserLocation.slice(0, idx);
            browserLocation += '/index.jsp';
        }
    }
    browserLocation += '?target=' + currentpage;
    $("#directlinktextbox").val(browserLocation);
}


function toggle(element)
{
    var x = document.getElementById(element);
    if (x != null)
    {
        if (x.style.display == "none")
            x.style.display = "";
        else
            x.style.display = "none";
    }
}


var alertreloader;
function stopCheckAlerts()
{
    if (alertreloader != null && alertreloader != undefined)
    {
        clearTimeout(alertreloader);
    }
}
function CheckAlerts()
{
    if (alertreloader == null || alertreloader == undefined)
    {
        var alerti = getCookie('alertsInterval');
        var x = getCookie('alertsEnabled');
        if (x == null || x == false)
            return;
        DoAlerts();
        if (alerti == null || alerti < 1000)
        {
            alerti = 30000;
            setCookie('alertsInterval', alerti, '9999');
        }
        alertreloader = setTimeout(function()
        {
            window.console && console.log('Alerts Refreshing');
            DoAlerts();
        }, alerti);

    }
}

/*This function will check for recent SLA alerts
 */
function DoAlerts()
{
    var div = 'alerts';
    var link = 'reporting/alerts.jsp';

    window.console && console.log('loadpage ' + link);

    var request = $.ajax({
        url: link,
        type: "GET",
        cache: false
    });

    request.done(function(msg) {
        window.console && console.log('loadpage done ' + link);
        $("#" + div).html(msg);
        $(this).css('cursor', 'auto');
        $('#' + div).show();
    });

    request.fail(function(jqXHR, textStatus) {
        window.console && console.log('loadpage failed ' + link);
        if (textStatus.indexOf("Please Login") > -1)
        {
	    window.console && console.log('autologout 5');
            window.location.href = "index.jsp";
            return;
        }
        $("#" + div).html('Error loading alerts! ' + jqXHR.responseText);
        $('#' + div).show();
    });


}



var reloader;
function toggleStatusReloader()
{
    if (reloader == null || reloader == undefined)
    {
        reloader = setTimeout(function()
        {
            window.console && console.log('StatusPage Refreshing');
            loadpage('status.jsp', 'mainpane');
        }, 30000);
        $("#reloaderButton").text("Automatic Reloads Enabled");
        loadpage('status.jsp', 'mainpane');
    }
    else
    {
        clearTimeout(reloader);
        reloader = null;
        $("#reloaderButton").text("Automatic Reloads Disabled");
    }
}


function toggleVisibility(str)
{
    var x = document.getElementById(str);
    if (x.style.display == "none")
        x.style.display = "";
    else
        x.style.display = "none";
//return false;
}



function StringCompare(a, b)
{
    return (a == b);
}

function StringSort(a, b)
{
    if (a > b)
        return 1;
    if (a < b)
        return -1;
    return 0;
}


function sortfunction(a, b) {
    //Compare "a" and "b" in some fashion, and return -1, 0, or 1
    if (a['x'] > b['x'])
        return 1;
    if (a['x'] < b['x'])
        return -1;
    return 0;
}
function compareFunction(a, b)
{
    return (a['x'] == b['x'])
}

/**
 * Removes duplicates from an array of items. Does not return any
 * value as it removes duplicates from the original array itself. Use
 * of a comparator allows for comparing complex items. For example:
 *
 * <code>
 * var a = [ 2, 1, 1, 5 ];
 * a.dedupe({
 *  sortComparator: function(a, b) {
 *      if (a > b)
 *          return 1;
 *      else if (a < b)
 *          return -1;
 *      else
 *          return 0;
 *  },
 *  dedupeComparator: function(a, b) {
 *      return a === b;
 *  }
 * });
 * </code>
 * @author Adam Presley
 *
 * @param comparators An object containing two keys: sortComparator and
 *  dedupeComparator. Both are functions taking two arguments. See
 *  the sort method for information on the sortComparator. The 
 *  dedupeComparator takes two arguments, and expects a true/false
 *  return on if the items are the same or not.
 */
Array.prototype.dedupe = function(comparators) {
    this.sort(comparators.sortComparator);

    var length = this.length, index = 0;
    var spliceStart = 0, spliceLen = 0;
    var item = 0, compareResult = 0, temp = 0;

    if (length > 1) {
        while (index < length - 1) {
            item = this[index];

            if (index + 1 < length) {
                spliceStart = index + 1;
                spliceLen = 0;
                temp = this[spliceStart];

                while ((compareResult = comparators.dedupeComparator.call(this, item, temp))) {
                    spliceLen++;

                    if (spliceStart + spliceLen >= length - 1)
                        break;
                    temp = this[spliceStart + spliceLen];
                }

                if (spliceLen > 0) {
                    this.splice(spliceStart, spliceLen);
                    length -= spliceLen;
                }
            }

            index++;
        }
    }
};


function sortfunction(a, b) {
    //Compare "a" and "b" in some fashion, and return -1, 0, or 1
    if (a['x'] > b['x'])
        return 1;
    if (a['x'] < b['x'])
        return -1;
    return 0;
}
function compareFunction(a, b)
{
    return (a['x'] == b['x'])
}

/**
 * Removes duplicates from an array of items. Does not return any
 * value as it removes duplicates from the original array itself. Use
 * of a comparator allows for comparing complex items. For example:
 *
 * <code>
 * var a = [ 2, 1, 1, 5 ];
 * a.dedupe({
 *  sortComparator: function(a, b) {
 *      if (a > b)
 *          return 1;
 *      else if (a < b)
 *          return -1;
 *      else
 *          return 0;
 *  },
 *  dedupeComparator: function(a, b) {
 *      return a === b;
 *  }
 * });
 * </code>
 * @author Adam Presley
 *
 * @param comparators An object containing two keys: sortComparator and
 *  dedupeComparator. Both are functions taking two arguments. See
 *  the sort method for information on the sortComparator. The 
 *  dedupeComparator takes two arguments, and expects a true/false
 *  return on if the items are the same or not.
 */
Array.prototype.dedupe = function(comparators) {
    this.sort(comparators.sortComparator);

    var length = this.length, index = 0;
    var spliceStart = 0, spliceLen = 0;
    var item = 0, compareResult = 0, temp = 0;

    if (length > 1) {
        while (index < length - 1) {
            item = this[index];

            if (index + 1 < length) {
                spliceStart = index + 1;
                spliceLen = 0;
                temp = this[spliceStart];

                while ((compareResult = comparators.dedupeComparator.call(this, item, temp))) {
                    spliceLen++;

                    if (spliceStart + spliceLen >= length - 1)
                        break;
                    temp = this[spliceStart + spliceLen];
                }

                if (spliceLen > 0) {
                    this.splice(spliceStart, spliceLen);
                    length -= spliceLen;
                }
            }

            index++;
        }
    }
};



function ShowDateHelp()
{

    window.open('help/timeformats.jsp', '_blank', 'fullscreen=false, height=300, width=500, menubar=false, location=false, resizable=yes, scrollbars=yes, status=false, titlebar=false');
}
function ShowHelp(str)
{

    window.open('help/' + str + '.jsp', '_blank', 'fullscreen=false, height=300, width=500, menubar=false, location=false, resizable=yes, scrollbars=yes, status=false, titlebar=false');
}

function PromptAlert()
{
    alert("Enter your own disclaimer here, if needed");

}

var workcounter = 0;

function toggleVisibilitySLAAction()
{


    var x = document.getElementById("sendmail");
    if (x.style.display == "none")
        x.style.display = "";
    else
        x.style.display = "none";

    x = document.getElementById("logger");
    if (x.style.display == "none")
        x.style.display = "";
    else
        x.style.display = "none";



    return false;

}


/**
 * 
 * @param {type} f the method, i.e. 'save_profile'
 * @param {type} url destination url
 * @returns {undefined}
 */
function postBack(f, url)
{
    if (workcounter > 0)
    {
        //   alert("You have other pending requests, please wait...");
        //   return;
    }
    //workcounter++;
    window.console && console.log('postback ' + f + ' ' + url);
    $(this).css('cursor', 'progress');

    $("#resultBar").html('<img src="img/ajax-loader.gif">');
    $("#resultBar").show();

    var form = $("#form1");
    var d = form.serializeArray();
    d.push({
        name: f,
        value: true
    });
    var request = $.ajax({
        url: url,
        type: "POST",
        //  dataType: "html", 
        cache: false,
        //  processData: false,f
        data: d
    });


    request.done(function(msg) {
        window.console && console.log('postback done ' + f + ' ' + url);
        if (msg.indexOf("Please Login") != -1)
        {
	    window.console && console.log('autologout 6');
            window.location.href = "index.jsp";
            return;
        }
        $("#resultBar").html(msg + '<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;' + '</a>');
        $("#resultBar").show();
        $(this).css('cursor', 'auto');
        //    workcounter--;
    });

    request.fail(function(jqXHR, textStatus) {
        window.console && console.log('postback failed ' + f + ' ' + url);
        if (textStatus.indexOf("Please Login") > -1)
        {
	     window.console && console.log('autologout 7');
            window.location.href = "index.jsp";
            return;
        }
        $("#resultBar").html(jqXHR.responseText + '<a class="close" data-dismiss="alert" href="javascript:hideAlert();">&times;' + '</a>');
        //$(".alert").alert();
        $("#resultBar").show();
        $(this).css('cursor', 'auto');
        //  workcounter--;
    });
}

function hideAlert()
{
    $("#resultBar").hide();
}
//used for the profile editor
function postBackReRender(f, url, div)
{
    if (workcounter > 0)
    {
        //     alert("You have other pending requests, please wait...");
        //   return;
    }
    currentpage = url;
    //workcounter++;
    window.console && console.log('postbackrerender ' + f + ' ' + url);
    $(this).css('cursor', 'progress');
    var form = $("#form1");
    var d = form.serializeArray();
    d.push({
        name: f,
        value: true
    });
    $("#" + div).html('<img src="img/ajax-loader.gif">');
    var request = $.ajax({
        url: url,
        type: "POST",
        //  dataType: "html", 
        cache: false,
        //  processData: false,f
        data: d
    });


    request.done(function(msg) {
        window.console && console.log('postbackrerender done ' + f + ' ' + url);
        $("#" + div).html(msg);
        $("#accord").tabs();
        $(this).css('cursor', 'auto');
        //     workcounter--;
    });

    request.fail(function(jqXHR, textStatus) {
        window.console && console.log('postbackrerender failed ' + f + ' ' + url);
        if (textStatus.indexOf("Please Login") > -1)
        {
	    window.console && console.log('autologout 8');
            window.location.href = "index.jsp";
            return;
        }
        $("#" + div).html(jqXHR.responseText);
        $("#accord").tabs();
        $(this).css('cursor', 'auto');
        //   workcounter--;
    });
}



//function loadpage(link, div)
function loadpage(link, div, parent)
{
    $('#result').hide();
    if (workcounter > 0)
    {
        //  alert("You have other pending requests, please wait...");
        //return;
    }
    currentpage = link;
    destroyCharts();


    //workcounter++;
    window.console && console.log('loadpage ' + link);
    $(this).css('cursor', 'progress');
    var debug = getCookie("DEBUG");
    if (debug != null)
        alert('hi' + link + div);

    var request = $.ajax({
        url: link,
        type: "GET",
        cache: false
    });

    request.done(function(msg) {
        //  ready = true; 
        breadcrumbing(link, parent);
        window.console && console.log('loadpage done ' + link);
        if (new String(msg).indexOf("Please Login") > -1)
        {
	     //window.console && console.log('autologout 1 ' + msg);
            //workcounter--;
            //window.location.href = "index.jsp";
            //return;
        }
        $("#" + div).html(msg);
        $(this).css('cursor', 'auto');
        // workcounter--;
        $(function() {
            $("#fromdatepicker").datepicker();
        });
        $(function() {
            $("#todatepicker").datepicker();
        });
        var x = $("#reloaderButton");
        if (reloader == null || reloader == undefined)
        {
            if (x != null)
                x.text("Automatic Reloads Enabled");
        }
        else
        {
            if (x != null)
                x.text("Automatic Reloads Disabled");
        }
    });

    request.fail(function(jqXHR, textStatus) {
        window.console && console.log('loadpage failed ' + link);
        if (textStatus.indexOf("Please Login") > -1)
        {
	     window.console && console.log('autologout 2' + textStatus + jqXHR.responseText);
            window.location.href = "index.jsp";
            return;
        }
        $("#" + div).html(jqXHR.responseText);

        $(this).css('cursor', 'auto');
        // workcounter--;
        $(function() {
            $("#fromdatepicker").datepicker();
        });
        $(function() {
            $("#todatepicker").datepicker();
        });
    });

    /*
     $.get(link, function (data) {
     $(this).css('cursor', 'auto');                
     if (debug!=null)
     alert('success');
     $("#" + div).html(data);
     $(function(){
     $( "#fromdatepicker").datepicker();
     });
     $(function(){
     $( "#todatepicker").datepicker();
     });
     });*/
}

function loadpage_params(link, div)
{

    if (workcounter > 0)
    {
        //   alert("You have other pending requests, please wait...");
        // return;
    }
    // workcounter++;
    destroyCharts();



    window.console && console.log('loadpage_params params' + link);
    var debug = getCookie("DEBUG");
    if (debug != null)
        alert('hi' + link + div);

    var request = $.ajax({
        url: link,
        type: "GET",
        cache: false
    });

    request.done(function(msg) {
        window.console && console.log('loadpage_params done ' + link);
        if (msg.indexOf("Please Login") != -1)
        {
	     window.console && console.log('autologout 3');
            window.location.href = "index.jsp";
            return;
        }
        $("#" + div).html(msg);
        $(this).css('cursor', 'auto');
        //     workcounter--;

    });

    request.fail(function(jqXHR, textStatus) {
        window.console && console.log('loadpage_params failed ' + link);
        if (textStatus.indexOf("Please Login") > -1)
        {
	     window.console && console.log('autologout 4');
            window.location.href = "index.jsp";
            return;
        }
        $("#" + div).html(jqXHR.responseText);

        $(this).css('cursor', 'auto');
        //   workcounter--;

    });



    /*$.get(link, function (data) {
     if (debug!=null)
     alert('success');
     $("#" + div).html(data);
     });*/
}

function getScreenHeight()
{
    var winH = 460;
    if (document.body && document.body.offsetWidth) {
        winH = document.body.offsetHeight;
    }
    if (document.compatMode == 'CSS1Compat' &&
            document.documentElement &&
            document.documentElement.offsetWidth) {
        winH = document.documentElement.offsetHeight;
    }
    if (window.innerWidth && window.innerHeight) {
        winH = window.innerHeight;
    }
    return winH;
}
function getScreenWidth()
{
    var winW = 630;
    if (document.body && document.body.offsetWidth) {
        winW = document.body.offsetWidth;
    }
    if (document.compatMode == 'CSS1Compat' &&
            document.documentElement &&
            document.documentElement.offsetWidth) {
        winW = document.documentElement.offsetWidth;

    }
    if (window.innerWidth && window.innerHeight) {
        winW = window.innerWidth;

    }
    return winW;
}
function setCookie(name, value, days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        var expires = "; expires=" + date.toGMTString();
    }
    else
        var expires = "";
    document.cookie = name + "=" + value + expires + "; path=/";
}

function getCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ')
            c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) == 0)
            return c.substring(nameEQ.length, c.length);
    }
    return null;
}

function deleteCookie(name) {
    setCookie(name, "", -1);
}
/*
 Changed function names from readCookie(), createCookie()
 and eraseCookie() to getCookie(), setCookie() and
 deleteCookie().
 */

function Show(id)
{
    var x = document.getElementById(id);
    if (x != null)
    {
        x.style.display = "";
    }
}
function Hide(id)
{
    var x = document.getElementById(id);
    if (x != null)
    {
        x.style.display = "none";
    }
}

function Adjust()
{
    //  alert("adjust");
    //mainpane
    //servicelistbar

    $("mainpane").addClass("span8");
    $("mainpane").removeClass("span12");

    $("servicelistbar").addClass("span4");
    $("servicelistbar").removeClass("span1");
}
function Unadjust()
{
    //    alert("unadjust");
    $("mainpane").addClass("span12");
    $("mainpane").removeClass("span8");

    $("servicelistbar").addClass("span1");
    $("servicelistbar").removeClass("span4");
}

var visited;
function checkCookie()
{
    visited = getCookie("visited");
    if (visited == null || visited == "")
    {
        PromptAlert();
        setCookie("visited", "true", 1);
    }

    var temp = document.location.href;
    if (temp.substring(0, 5) === "https")
    {

    }
    else
    {
        sslwarn = getCookie("sslwarn");
        if (sslwarn == null || sslwarn == "")
        {
            alert("Warning! You are viewing this site over a non-secure connection. Any data that is transmitted will be visibile by others and you may be vulernable to CSRF attacks. Consider using HTTPS instead of HTTP.");
            setCookie("sslwarn", "ok", 1);

        }
    }
}




function toggleVisibilitySLARule()
{


    //slatimerange
    //slaparameter
    var x = document.getElementById('rule1');
    if (x == null || x == undefined)
        return;
    var str = x.value;

    //TODO handle and or not



    if (str == "SLARuleGeneric")
    {
        //    alert ('none');
        //hide all parameters
        var y = document.getElementById('timerange');
        y.style.display = "none";
        y = document.getElementById('slaparameter');
        y.style.display = "none";
        y = document.getElementById('slaparameterpartition');
        y.style.display = "none";
        y = document.getElementById('SLARuleGenericdiv');
        y.style.display = "";
        y = document.getElementById('xpathdiv');
        y.style.display = "none";
    }



    ShowSLAHelp();

}

//@deprecated
function ShowSLAHelp()
{
    var x = document.getElementById('rule1');
    var str = x.value;
    var strhelp = "Help!";
    //HighThreadCount
    //HighOpenFileHandles
    switch (str)
    {
        case "AllFaults":
            strhelp = "This triggers when a transactional service faults.";
            break;
        case "AllSuccess":
            strhelp = "This triggers when a transactional service succeeds.";
            break;
        case "ResponseTimeGreaterThan":
            strhelp = "This triggers when a transactional service takes longer than n milliseconds to response. Enter the value in milliseconds.";
            break;
        case "ResponseTimeLessThan":
            strhelp = "This triggers when a transactional service takes less than n milliseconds to response. Enter the value in milliseconds.";
            break;
        case "RequestMessageSizeGreaterThan":
            strhelp = "This triggers when a transactional service request message is larger than n bytes. Enter the value in bytes.";
            break;
        case "ResponseMessageSizeGreaterThan":
            strhelp = "This triggers when a transactional service response message is larger than n bytes. Enter the value in bytes.";
            break;
        case "RequestMessageSizeLessThan":
            strhelp = "This triggers when a transactional service request message is less than n bytes. Enter the value in bytes.";
            break;
        case "ResponseMessageSizeLessThan":
            strhelp = "This triggers when a transactional service response message is less than n bytes. Enter the value in bytes.";
            break;
        case "ActionContainsIgnoreCase":
            strhelp = "This triggers when a transactional service action contains the specified string, ignore the case. Actions can sometimes represent the method that was invoked or HTTP methods such as GET or POST";
            break;
        case "ActionEqualsIgnoreCase":
            strhelp = "This triggers when a transactional service action equals the specified string, ignore the case. Actions can sometimes represent the method that was invoked or HTTP methods such as GET or POST";
            break;
        case "XPathExpression":
            strhelp = "This rule performs an XPath Query on either the request or response of a transactional service. XPaths enable you to search for a specific key word or XML element. This requires that the service's policy includes record request or response based on what you're looking for. For .NET base agents, a listing of all XML Namespaces is required. Java based agents do not require this. Use this with caution as it is CPU intensive operation. In additional, the request or response message must be less than the maximum capture size of 1 MB.";
            break;
        case "FaultsOverTimeGreaterThan":
            strhelp = "This triggers when a transactional service has more than x number of faults in the given period of time. These rules are processed at the FGSMS server(s) via the SLA processor. It runs by default every 5 minutes using the data tallied by the Statistic Aggregator.";
            break;
        case "FaultsOverTimeLessThan":
            strhelp = "This triggers when a transactional service has less than x number of faults in the given period of time. These rules are processed at the FGSMS server(s) via the SLA processor. It runs by default every 5 minutes using the data tallied by the Statistic Aggregator.";
            break;
        case "InvocationsOverTimeGreatThan":
            strhelp = "This triggers when a transactional service has more than x number of successful messages in the given period of time. These rules are processed at the FGSMS server(s) via the SLA processor. It runs by default every 5 minutes using the data tallied by the Statistic Aggregator.";
            break;
        case "InvocationsOverTimeLessThan":
            strhelp = "This triggers when a transactional service has less than x number of successful messages in the given period of time. These rules are processed at the FGSMS server(s) via the SLA processor. It runs by default every 5 minutes using the data tallied by the Statistic Aggregator.";
            break;
        case "MeanTimeBetweenFailureGreatThan":
            strhelp = "This triggers when a transactional service has a mean time between failure greater than the specified period of <a href=\"javascript:ShowDateHelp();\">time</a>. These rules are processed at the FGSMS server(s) via the SLA processor. It runs by default every 5 minutes using the data tallied by the Statistic Aggregator.";
            break;
        case "MeanTimeBetweenFailureLessThan":
            strhelp = "This triggers when a transactional service has a mean time between failure greater than the specified period of <a href=\"javascript:ShowDateHelp();\">time</a>. These rules are processed at the FGSMS server(s) via the SLA processor. It runs by default every 5 minutes using the data tallied by the Statistic Aggregator.";
            break;
        case "ConsumerEqualsIgnoreCase":
            strhelp = "This triggers when a transactional service customer equals the specified string, ignore the case. The consumer list can contain IP addresses, usernames or certificate distinguished names.";
            break;
        case "ConsumerContainsIgnoreCase":
            strhelp = "This triggers when a transactional service customer contains the specified string, ignore the case. The consumer list can contain IP addresses, usernames or certificate distinguished names.";
            break;
        case "TransactionalAgentMemoContainsIgnoreCase":
            strhelp = "This triggers when a transactional service agent's memo contains the specified string, ignore the case. Agents occasionally send container information and agent status.";
            break;
        case "ChangeInAvailabilityStatus":
            strhelp = "This triggers when a service status changes from operational to not and back again.";
            break;
        case "BrokerQueueSizeGreaterThan":
            strhelp = "This triggers when a statistical service (message brokers) has a queue size greater than the specified value. This can indicate that a persistent topic's subscribers are not functioning.";
            break;
        case "QueueOrTopicDoesNotExist":
            strhelp = "This triggers when a statistical service (message brokers) queue or topic list does not contain the specified string (contains ignoring case)";
            break;
        case "LowDiskSpace":
            strhelp = "This triggers when a machine's free disk space is lower than the specified value in megabytes for the specific drive or partition. The setting Monitor Free Disk Space must be specified. This is useful for systems with heavy logging or auditing.";
            break;
        case "HighCPUUsageOverTime":
            strhelp = "This triggers when a the CPU usage for a machine or process is higher than the specified percentage (obmit the % symbol) for the specified period of <a href=\"javascript:ShowDateHelp();\">time</a>. These rules are processed at the FGSMS server(s) via the SLA processor. It runs by default every 5 minutes using the data tallied by the Statistic Aggregator.";
            break;
        case "HighCPUUsage":
            strhelp = "This triggers when a the CPU usage for a machine or process is higher than the specified percentage (obmit the % symbol).";
            break;
        case "HighMemoryUsage":
            strhelp = "This triggers when a the memory usage for a machine or process is higher than the specified percentage (obmit the % symbol).";
            break;
        case "HighMemoryUsageOverTime":
            strhelp = "This triggers when a the memory usage for a machine or process is higher than the specified percentage (obmit the % symbol) for the specified period of <a href=\"javascript:ShowDateHelp();\">time</a>. These rules are processed at the FGSMS server(s) via the SLA processor. It runs by default every 5 minutes using the data tallied by the Statistic Aggregator.";
            break;
        case "HighDiskUsage":
            strhelp = "This triggers when a the disk usage for a machine or process is higher than the specified rate (kilobytes per second)";
            break;
        case "HighDiskUsageOverTime":
            strhelp = "This triggers when a the disk usage for a machine or process is higher than the specified rate (kilobytes per second) for the specified period of <a href=\"javascript:ShowDateHelp();\">time</a>. These rules are processed at the FGSMS server(s) via the SLA processor. It runs by default every 5 minutes using the data tallied by the Statistic Aggregator.";
            break;
        case "HighNetworkUsage":
            strhelp = "This triggers when a the network usage for a machine or process is higher than the specified rate (kilobytes per second).  This depends in the agent's capabilities meaning that not all agents support this feature.";
            break;
        case "HighNetworkUsageOverTime":
            strhelp = "This triggers when a the network usage for a machine or process is higher than the specified rate (kilobytes per second) for the specified period of <a href=\"javascript:ShowDateHelp();\">time</a>. This depends in the agent's capabilities meaning that not all agents support this feature. These rules are processed at the FGSMS server(s) via the SLA processor. It runs by default every 5 minutes using the data tallied by the Statistic Aggregator.";
            break;
        case "StatusAgentMessageContainsIgnoreCase":
            strhelp = "This triggers when a service status message contains the specific string, ignoring case.";
            break;
        case "LowDiskSpace":
            strhelp = "This triggers when a the free hard drive space on the specified partition or drive is less than the parameter in megabytes. In order for any actions to trigger, you must also use the 'Monitor Free Disk Space' feature in the Process tab.";
            break;
        case "HighThreadCount":
            strhelp = "This triggers when a the number of threads for either a machine or a process exceeds the given parameter.";
            break;
        case "HighOpenFileHandles":
            strhelp = "This triggers when a the number of number of open files for a process exceeds the given parameter.";
            break;
        case "SLARuleGeneric":
            strhelp = "This rule can be used by developers and system integrators to bring their own application or business logic into FGSMS.  The implementing Java class name must be specified using the parameter box. All other settings are optional. See the SDK for details.";
            break;
        case "StaleData":
            strhelp = "This rule is used for when there is either a problem with the server hosting the agent, or the agent itself and triggers when status information is determined to be stale (usually older than 5 minutes). Use the time range field to override the default setting of 5 minutes. Administrators can set the default value through general settings";
            //HighThreadCount
            //HighOpenFileHandles      
    }
    document.getElementById("slaparameterhelp").innerHTML = strhelp;


}


function refreshPluginParams(pluginselect, targetdiv, plugintype) {
    var classname = $("#" + pluginselect).val();
    if (classname === "")
        return;
    // var plugintype="FEDERATION_PUBLISH";
    loadpageParam("profile/getPluginParametersEditable.jsp?classname=" + classname + "&plugintype=" + plugintype, targetdiv);
}


function refreshPluginParamsWithCallback(pluginselect, targetdiv, plugintype, callback, id) {
    var classname = $("#" + pluginselect).val();
    if (classname === "")
        return;
    // var plugintype="FEDERATION_PUBLISH";
    loadpageParam("profile/getPluginParametersEditable.jsp?classname=" + classname + "&plugintype=" + plugintype, targetdiv, callback,id);
}

function getPluginDisplayName(classname, targetdiv, plugintype, callback, id) {
    if (classname===null || classname === "")
        return;
    loadpageParam("profile/getPluginDisplayName.jsp?classname=" + classname + "&plugintype=" + plugintype, targetdiv, callback,id);
}


function loadpageParam(link, div, callback,id)
{
    window.console && console.log('loadpage ' + link);
    $(this).css('cursor', 'progress');
    var debug = getCookie("DEBUG");
    var request = $.ajax({
        url: link,
        type: "GET",
        cache: false
    });
    

    request.done(function(msg) {
        window.console && console.log('loadpage done ' + link);
        if (msg.indexOf("Please Login") != -1)
        {
            window.location.href = "index.jsp";
            return;
        }
        $("#" + div).html(msg);
	if ( callback !==undefined && callback!==null)
	     callback(id);
    });

    request.fail(function(jqXHR, textStatus) {
        window.console && console.log('loadpage failed ' + link);
        if (textStatus.indexOf("Please Login") > -1)
        {
            window.location.href = "index.jsp";
            return;
        }
        $("#" + div).html(jqXHR.responseText);
    });



}

/**
 * http://stackoverflow.com/questions/105034/create-guid-uuid-in-javascript
 
 * @returns {String} */
function newUuid() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}


/**
 * returns a jquery escaped string
 * @param {type} str
 * @returns {unresolved} */
function escapeJQuerySelctor(str) 
{
    if (str)
//	    var val = $(this).attr('val').replace(/[!"#$%&'()*+,.\/:;<=>?@[\\\]^`{|}~]/g, "\\\\$&")

        return str.replace(/%([ #;?%&,.+*~\':"!^$[\]()=>|\/@])/g,'\\$1');      

    return str;
}

var entityMap = {
  '&': '&amp;',
  '<': '&lt;',
  '>': '&gt;',
  '"': '&quot;',
  "'": '&#39;',
  '/': '&#x2F;',
  '`': '&#x60;',
  '=': '&#x3D;'
};

function escapeHtml(string) {
  return String(string).replace(/[&<>"'`=\/]/g, function (s) {
    return entityMap[s];
  });
}

function formatDateTime(timeinms) {
    
    //yyyy-MM-dd'T'HH:mm:ss.SSSZ
    
    
    
    var d=new Date(timeinms);
    return  d.toISOString();    //2017-01-27T23:40:42.829Z
    
    
}