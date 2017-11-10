/**
 *This file contains all charting and json related quiries
 */

function addAvailabilityData(url, renderto, poll)
{
    $.getJSON('profile/getAvailabilityJson.jsp?url=' + url, function(data)
    {
        insertChartDataAvail(url, renderto, data);
    });
}

var availabilitychart=null;
var transactionschart=null;

function insertChartDataAvail(url, renderto, data)
{
    var series = [];
    $.each(data, function(key1, val2) {
        //first node is the url
        $.each(val2, function(key2, val3) {
            //for each transaction
            var timestamp=null;
      
            var lastStatusChange=null;
            var operational=null;
            var message=null;
            $.each(val3, function(key3, val4) {
                //for each transaction key3 = the transaction id
                $.each(val4, function(key4, val5) {
                    if (key4=="timestamp")
                        timestamp = val5;
                    if (key4=="lastStatusChange")
                        lastStatusChange= val5;
                    if (key4=="operational")
                        operational= val5;
                    if (key4=="message")
                        message = val5;
                
                });
                if (timestamp!=null && operational!=null)
                {
                    var color="#00FF00";
                    if (!operational) //if fault
                        color="#FF0000";
                    var yval=1;
                    if (!operational)
                        yval=0;
                    series.push({
                        x:timestamp, 
                        y:yval, 
                        color:color,
                        recordedat:timestamp,
                        transactionid:key3,
                        fgsms:'aid',
                        policyurl:key1,
                        marker :{
                            shape : 'circlepin',
                            width : 16,
                            enabled:true,
                            fillColor:color
                        }
                    });
                }
            });
                     
        });
    });
       
    if (series.length == 0)				                
    {
        window.console && 
        console.log('no availability data returned for ' + url);
        return;
    }
    else 
    {
        window.console && 
        console.log(series.length + ' availability data items returned for ' + url);
    }
    series.dedupe({
        sortComparator: sortfunction, 
        dedupeComparator:compareFunction
    });
    //series.sort(sortfunction);
    if (availabilitychart==null)
    {
        window.console && 
        console.log('chart is null, creating');
        availabilitychart=createChart(series, url, renderto, true, 'Availability','Status');
    }
    else
    {
        window.console && 
        console.log('chart exists, checking if series Availability ' + url + 'exists');
        //locate the series list and try to find an existing series of the same url    
        var idx=-1
        var allseries = availabilitychart.series;
        window.console && 
        console.log(series.length + ' series exist');
        $.each(allseries, function(ds)
        {
            //console.log('debug(url,name)' + allseries[ds].url + allseries[ds].name);
            if (allseries[ds].name == url )
            {
                idx = ds;
            }
        });
        if (idx >= 0)
        {
            window.console && 
            console.log('series Availability ' + url + ' exists, adding the new data points');
            //series exists, just add the data points
            $.each(series, function(x){
                allseries[idx].addPoint(series[x], false, true);
            });
            availabilitychart.redraw();
        }
        else{   //series does not exist, add it
            window.console && 
            console.log('series Availability ' + url + ' does not exists, adding the new series');
            availabilitychart.addSeries({
                data: series, 

                name:url,
                url: url,
                shape : 'circlepin',
                step:true,
                width : 16
            }, true, true);
        }
    }					
	
}

function destroyCharts()
{
    if (transactionschart!=undefined && transactionschart != null  )
    {
        transactionschart.destroy();
        transactionschart=null;
    }
    if ( percentscale!=undefined&&percentscale != null)
    {
        percentscale.destroy();
        percentscale=null;
    }
    if ( networkdiskchart!=undefined && networkdiskchart != null)
    {
        networkdiskchart.destroy();
        networkdiskchart=null;
    }
    if ( threadsfilecharts!=undefined && threadsfilecharts != null)
    {
        threadsfilecharts.destroy();
        threadsfilecharts=null;
    }
    if ( availabilitychart!=undefined && availabilitychart != null)
    {
        availabilitychart.destroy();
        availabilitychart=null;
    }
    
}


//used for performance page
var poll=null;
//'transactions','availability','counters','resource'
function updatechart(transactionchart, availabilitychart, counterchart, percentagechart, iochart)
{
    window.console &&   console.log('updating performance chart');
    if (poll==undefined || poll==null )
    {
        pol = Date.now() - (15 *60 * 1000);
    }
    var checkedbox = $("form input:checkbox");
    $.each (checkedbox, function(data)
    {
        var thisurl = checkedbox[data].value;
        var t=null;
        if (thisurl.lastIndexOf('wsperf:',0)===0)
        {
            thisurl=thisurl.substring(7, checkedbox[data].value.length);
            t='wsperf';
        }
        if (thisurl.lastIndexOf('mpperf:',0)===0)
        {
            thisurl=thisurl.substring(7, checkedbox[data].value.length);
            t='mpperf';
        }
        if (thisurl.lastIndexOf('ppperf:',0)===0)
        {
            thisurl=thisurl.substring(7, checkedbox[data].value.length);
            t='ppperf';
        }
        if (thisurl.lastIndexOf('statperf:',0)===0)
        {
            thisurl=thisurl.substring(9, checkedbox[data].value.length);
            t='statperf';
        }
        if (thisurl.lastIndexOf('avail:',0)===0)
        {
            thisurl=thisurl.substring(6, checkedbox[data].value.length);
            t='avail';
        }
            
        if (checkedbox[data].checked)
        {
            if (t=='wsperf')
            {
                addData4(thisurl, transactionchart, pol);
            }
            else if (t=='avail')
            {

                var x = thisurl.split('|', 2);
                if (x.length==2)
                    addAvailabilityData(x[0], availabilitychart, pol);
                else
                    addAvailabilityData(thisurl, availabilitychart, pol);
            } else if (t=='statperf')
{
                createBrokerCharts(thisurl, counterchart,pol);
            }
            else if (t=='mpperf')
            {
                var x = thisurl.split('|', 2);
                window.console &&   console.log('updating machine perf '+ x[0] + ' ' + x[1]);
                //   addAvailabilityData(x[0], availabilitychart, pol);
                createCharts(x[0], x[1], percentagechart, counterchart, iochart,pol) ;
            }
            else if (t=='ppperf')
            {
                var x = thisurl.split('|', 2);
                window.console &&   console.log('updating machine perf '+ x[0] + ' ' + x[1]);
                createProcessCharts(x[0], x[1], percentagechart, counterchart,pol);
            }
            else
            { 
            //                alert (checkedbox[data].value + ' hasnt been written yet');
            }
        }
        else
        {
            removeSeries(thisurl);
        }
    });
    pol = Date.now();
}


function removeSeries(url)
{
    if (transactionschart!=null)
    {
        $.each(transactionschart.series, function (x)
        {
            try{
                if (transactionschart.series[x].name !=undefined )
                    window.console && console.log('transactionschart series  '+ transactionschart.series[x].name);
                if (transactionschart.series[x].name !=undefined && transactionschart.series[x].name.lastIndexOf(url,0)===0)
                {
                    transactionschart.series[x].remove();
                }
            }catch (x){}
        });
    }
    if (availabilitychart!=null)
    {
        $.each(availabilitychart.series, function (x)
        {
            try{
                if (availabilitychart.series[x].name !=undefined )
                    window.console && console.log('availabilitychart series  '+ availabilitychart.series[x].name);
                if (availabilitychart.series[x].name !=undefined && availabilitychart.series[x].name.lastIndexOf(url,0)===0)
                {
                    availabilitychart.series[x].remove();
                }
            }catch (x){}
        });
    }
    
    if (percentscale!= undefined && percentscale!=null)
    {
        $.each(percentscale.series, function (x)
        {
            try{
                if (percentscale.series[x].name !=undefined )
                    window.console && console.log('percentscale series  '+ percentscale.series[x].name);
                if (percentscale.series[x].name !=undefined && percentscale.series[x].name.lastIndexOf(url,0)===0)
                {
                    percentscale.series[x].remove();
                }
            }catch (x){}
        });
    }
    
    if (networkdiskchart!= undefined && networkdiskchart!=null)
    {
        $.each(networkdiskchart.series, function (x)
        {
            try{
                if (networkdiskchart.series[x].name !=undefined )
                    window.console && console.log('networkdiskchart series  '+ networkdiskchart.series[x].name);
                if (networkdiskchart.series[x].name !=undefined &&networkdiskchart.series[x].name.lastIndexOf(url,0)===0)
                {
                    networkdiskchart.series[x].remove();
                }
            }catch (x){}
        });
    }
    
    if (threadsfilecharts!= undefined && threadsfilecharts!=null)
    {
        $.each(threadsfilecharts.series, function (x)
        {
            try{
                if (threadsfilecharts.series[x].name !=undefined )
                    window.console && console.log('threadsfilecharts series  '+ threadsfilecharts.series[x].name);
                if (threadsfilecharts.series[x].name !=undefined && threadsfilecharts.series[x].name.lastIndexOf(url,0)===0)
                {
                    threadsfilecharts.series[x].remove();
                }
            }catch (x){}
        });
    }
}
   


/*
 *loads transactions data, adds model support for up to 150 records records since a given timestamp in epoch ms
 */
function addData4(url, renderto, since)
{
    $.getJSON('profile/getTransactionsJson2.jsp?url=' + encodeURI(url) + '&since=' + since, function(data)
    {
        addData3internal(url, renderto, data);
    });
}

/*
 *loads transactions data, adds model support for the last 150 records for a transaction
 *url = the url of the service we want data on 
 *renderto = the div for the chart
 *tablediv = the div to dump tabular log data to, optional
 */
function addData3(url, renderto, tablediv)
{
    $.getJSON('profile/getTransactionsJson.jsp?url=' + encodeURI(url), function(data)
    {
        addData3internal(url, renderto, data, tablediv);
    });
}
		
function addData3internal(url, renderto, data, renderTabularDataTo)
{

    //var items = [];
        
    var series = [];
    
    var thedata=[];
    thedata.push("<table class=\"table  table-hover table-striped\"><caption>Transaction logs for " + url + "</caption><tr><th>Transaction Id</th><th>Timestamp</th><th>Response Time</th><th>Fault</th></tr>");
        
    $.each(data, function(key1, val2) {
        //first node is the url
        $.each(val2, function(key2, val3) {
            //for each transaction
            var ts=null;
            var restime=null;
            var success=null;
            var action=null;
            var slafault=false;
            var requestpayloadavailable=false;
            var responsepayloadavailable=false;
            var recordedat=null;
							
            var consumer=null;
            var responsesize=null;
            var requestsize=null;
            $.each(val3, function(key3, val4) {
                //for each transaction key3 = the transaction id
                $.each(val4, function(key4, val5) {
                    if (key4=="responsetime")
                        restime = val5;
                    if (key4=="timestamp")
                        ts = val5;
                    if (key4=="success")
                        success= val5;
                    if (key4=="action")
                        action= val5;
                    if (key4=="slafault")
                        slafault = val5;
                    if (key4=="requestpayloadavailable")
                        requestpayloadavailable = val5;
                    if (key4=="responsepayloadavailable")
                        responsepayloadavailable = val5;

                    if (key4=="consumer")
                        consumer=val5;
                    if (key4=="recordedat")
                        recordedat=val5;
                    if (key4=="responsesize")
                        responsesize=val5;
                    if (key4=="requestsize")
                        requestsize=val5;
                });
                if (ts!=null && restime!=null)
                {
                    //thedata.push("<table border=1><tr><th>Transaction Id</th><th>Timestamp</th><th>Response Time</th><th>Fault</th></tr>");
                  
                    //                    console.log(key3 + ' ' + ts + ' ' + restime + ' ' + action + ' ' + success);
                      
                    var color="#00FF00";
                    if (success) //if fault
                        color="#FF0000";
                    if (slafault)
                        color="#FF6600";
                    if (!success)
                        //                    thedata.push('<tr class=\"success\"><td><a href="javascript:loadpage(\'reporting/SpecificTransactionLogViewer.jsp?ID=' + key3 + '\',\'mainpane\',\'profile.jsp?url=' + url +  '\');">' + 
                        thedata.push('<tr class=\"success\"><td><a href="javascript:loadpage(\'reporting/SpecificTransactionLogViewer.jsp?ID=' + key3 + '\',\'mainpane\');">' + 
                            key3 + '</td><td>' + formatDateTime(ts) + '</td><td>' + restime + '</td><td>' + success + '</td></tr>');
                    else if (slafault)
                        //thedata.push('<tr class=\"warn"><td><a href="javascript:loadpage(\'reporting/SpecificTransactionLogViewer.jsp?ID=' + key3 + '\',\'mainpane\',\'profile.jsp?url=' + url +  '\');">' + 
                            thedata.push('<tr class=\"warn"><td><a href="javascript:loadpage(\'reporting/SpecificTransactionLogViewer.jsp?ID=' + key3 + '\',\'mainpane\');">' + 
                            key3 + '</td><td>' + formatDateTime(ts) + '</td><td>' + restime + '</td><td>' + success + '</td></tr>');
                    else
                        //thedata.push('<tr class=\"error"><td><a href="javascript:loadpage(\'reporting/SpecificTransactionLogViewer.jsp?ID=' + key3 + '\',\'mainpane\',\'profile.jsp?url=' + url +  '\');">' + 
                        thedata.push('<tr class=\"error"><td><a href="javascript:loadpage(\'reporting/SpecificTransactionLogViewer.jsp?ID=' + key3 + '\',\'mainpane\');">' + 
                            key3 + '</td><td>' + formatDateTime(ts) + '</td><td>' + restime + '</td><td>' + success + '</td></tr>');
                    series.push({
                        x:ts, 
                        y:restime, 
                        color:color,
                        recordedat:recordedat,
                        transactionid:key3,
                        requestsize:requestsize,
                        responsesize:responsesize,
                        consumer:consumer,
                        fgsms:'tid',
                        slafault: slafault,
                        success: !success,
                        responsepayloadavailable:responsepayloadavailable,
                        requestpayloadavailable:requestpayloadavailable,
                        action:action,
                        policyurl:key1,
                        marker :{
                            enabled:true,
                            fillColor:color
                        }
                    });
                }
            });
                     
        });
    });
   
    thedata.push("</table>");
    if (renderTabularDataTo!=null)
        $('#' + renderTabularDataTo).html(thedata.join(''));
        
    if (series.length == 0)				                
    {
        window.console && 
        console.log('no data returned for ' + url);
        return;
    }
    else {
        window.console && 
        console.log(series.length + ' data items returned for ' + url);
    }
    series.dedupe({
        sortComparator: sortfunction, 
        dedupeComparator:compareFunction
    });
    //series.sort(sortfunction);
    if (transactionschart==null)
    {
        window.console && 
        console.log('chart is null, creating');
        transactionschart=createChart(series, url, renderto, false, 'Performance', 'Response Time (ms)');
    }
    else
    {
        window.console && 
        console.log('chart exists, checking if series ' + url + 'exists');
        //locate the series list and try to find an existing series of the same url    
        var idx=-1
        var allseries = transactionschart.series;
        window.console && 
        console.log(series.length + ' series exist');
        $.each(allseries, function(ds)
        {
            //console.log('debug(url,name)' + allseries[ds].url + allseries[ds].name);
            if (allseries[ds].name == url)
            {
                idx = ds;
            }
        });
        if (idx >= 0)
        {
            window.console && 
            console.log('series ' + url + ' exists, adding the new data points');
            //series exists, just add the data points
            $.each(series, function(x){
                allseries[idx].addPoint(series[x], false, true);
            });
            transactionschart.redraw();
        }
        else{   //series does not exist, add it
            window.console && 
            console.log('series ' + url + ' does not exists, adding the new series');
            transactionschart.addSeries({
                data: series, 
                name:url,
                url: url
            }, true, true);
        }
    }					
					
					

                
}

/**
*creates a transaction log chart
*/
function createChart(data, seriesname, renderto, step, chartTitle, yAxisTitle)
{

    var tmp = new Highcharts.StockChart({
        chart : {
            renderTo : renderto,
            type: 'spline'
									
        },
        plotOptions: {
            series: {
                cursor: 'pointer',
                events: {
                    click: function(event) {
                        // Log to console
                        //console.log(event.point);
                        
                        // this is for the modal popup
                        
                        /*alert(this.name +' clicked\n'+
                            'Alt: '+ event.altKey +'\n'+
                            'Control: '+ event.ctrlKey +'\n'+
                            'Shift: '+ event.shiftKey +'\n' +
                            'type: ' + event.point.fgsms + ' ' +
                            event.point.transactionid);*/
                        var date = new Date(event.point.x);
                        
                        $('#tid').text(event.point.transactionid);
                        $('#action').text(event.point.action);
                        $('#slafault').text(event.point.slafault);
                        $('#responsetime').text(event.point.y);
                        $('#timestamp').text(date.toISOString() + ' or time in epoch: ' + event.point.x );
                        $('#successfail').text(event.point.success);
                        $('#policyurl').text(event.point.policyurl);
                        $('#requesturl').text(event.point.requesturl);
                        $('#recordedat').text(event.point.recordedat);
												
                        $('#requestsize').text(event.point.requestsize);
                        $('#responsesize').text(event.point.responsesize);
                        $('#consumer').text(event.point.consumer);
												
                        $('#responsepayloadavailable').text(event.point.responsepayloadavailable);
                        $('#requestpayloadavailable').text(event.point.requestpayloadavailable);
                        $('#myModal').modal();
                    }
                }
            }
        },
        rangeSelector : {
            selected : 1,
            buttons: [
            {
                type: 'minute',
                count: 5,
                text: '5m'
            }, 
            {
                type: 'minute',
                count: 15,
                text: '15m'
            },
									   
            {
                type: 'hour',
                count: 1,
                text: '1hr'
            },
									   
									   
            {
                type: 'day',
                count: 1,
                text: '1d'
            }, {
                type: 'week',
                count: 1,
                text: '1w'
            }, {
                type: 'month',
                count: 1,
                text: '1m'
            },  {
                type: 'all',
                text: 'All'
            }]
		        
        },
        marker : {
            enabled : true,
            radius : 3
        },
        yAxis: [{
            min: 0,
            title: {
                text:yAxisTitle
            }
        }],
        title : {
            text : chartTitle
        },
				
        tooltip : {
            valueDecimals : 0
        },
        series : [{
            name : seriesname,
            url: seriesname,
            data : data,
            tooltip: {
                valueDecimals: 0
            }
            ,            
            step: step
        }
        //,flags
        ]
    //     step:false
    });
    return tmp;
}


/*
 *creates process charts
 */
function createProcessCharts(url, hostname, percentage, counters, since)
{
    //$.getJSON('profile/getTransactionsJson.jsp?url=' + url, function(data) {
    window.console &&   console.log('loading json data '+ 'reporting/processDataJson.jsp?uri=' + url );
    $.getJSON('reporting/processDataJson.jsp?uri=' + url + "&hostname=" + hostname + '&since=' + since, function(data){
        window.console &&   console.log('loading json process data success '+ 'reporting/machineDataJson.jsp?uri=' + url );
        var cpu=[];
        var mem=[];
        var files=[];
        var threads=[];
        
        $.each(data, function(key1, val2) {
            //first node is the url key1
            $.each(val2, function(key2, val3) {
                $.each(val3, function(key3, val4) {
                    var memp=0;
                    var cpup=0;
                    var thread=0;
                    var openfiles=0;
                    //key3 is the time stamp
                    var ts = Number(key3);
                    $.each(val4, function(key4, val5) {
                        //each for value 
                        if (key4=="mem")
                        {
                            memp = val5;
                        }
                        else if (key4=="threads")
                        {
                            thread=val5;
                        }
                        else   if (key4=="cpu")
                        {
                            cpup = val5;
                        }
                     
                        else if (key4=="openfiles")
                        {
                            openfiles = val5;
                        }
                        
                    });
                    
                    window.console &&   console.log('loading json process data cpu '+ cpup + ' thread' + thread + ' mem' + memp + ' at ' + key3 + ' ' + ts + ' ' + openfiles + ' ' + thread);
                    cpu.push({
                        x:ts, 
                        y:cpup, 
                        policyurl:key1
                    });
                    
                    threads.push({
                        x:ts, 
                        y:thread, 
                        policyurl:key1
                    });
                    
                    mem.push({
                        x:ts, 
                        y:memp, 
                        policyurl:key1
                    });
                    files.push({
                        x:ts, 
                        y:openfiles, 
                        policyurl:key1
                    })
                    
                });
                
                
            });
        });
        
        window.console &&   console.log("dedupe mem");
        mem.dedupe({
            sortComparator: sortfunction, 
            dedupeComparator:compareFunction
        });
        window.console &&   console.log("dedupe cpu");
        cpu.dedupe({
            sortComparator: sortfunction, 
            dedupeComparator:compareFunction
        });
        window.console &&   console.log("dedupe threads");
        threads.dedupe({
            sortComparator: sortfunction, 
            dedupeComparator:compareFunction
        });
        window.console &&   console.log("dedupe open files");
        files.dedupe({
            sortComparator: sortfunction, 
            dedupeComparator:compareFunction
        });
        

        window.console &&   console.log("creating mem cpu and thread charts");
        createMachineChart(mem, url + ' ' + "Memory%", percentage, 'Resource Utilization %','percentscale');
        createMachineChart(cpu, url + ' ' + "CPU%", percentage, 'Resource Utilization %','percentscale');
        createMachineChart(threads, url + ' ' + "Threads", counters, 'Resource Counters','threadsfilecharts');
        createMachineChart(files, url + ' ' + "Open Files", counters, 'Resource Counters','threadsfilecharts');
        
    
    });
        
    
  
}
/*create machine related charges
 */
function createCharts(url, machinename,percentage, counters,iochart, since)
{
    //$.getJSON('profile/getTransactionsJson.jsp?url=' + url, function(data) {
    window.console &&   console.log('loading json machine data '+ 'reporting/machineDataJson.jsp?uri=' + url + "&server=" + machinename);
    $.getJSON('reporting/machineDataJson.jsp?uri=' + url + "&server=" + machinename + '&since=' + since, function(data){
        window.console &&   console.log('loading json machine data success '+ 'reporting/machineDataJson.jsp?uri=' + url + "&server=" + machinename);
        var cpu=[];
        var mem=[];
        
        var threads=[];
        var freediskspace=new Object();
        freediskspace.data=[];
        var net=new Object();
        net.data = [];
        var disk=new Object();
        disk.data=[]
        $.each(data, function(key1, val2) {
            //first node is the url key1
            $.each(val2, function(key2, val3) {
                $.each(val3, function(key3, val4) {
                    var memp=0;
                    var cpup=0;
                    var thread=0;
                    //key3 is the time stamp
                    var ts = Number(key3);
                    $.each(val4, function(key4, val5) {
                        //each for value 
                        if (key4=="mem")
                        {
                            memp = val5;
                        }
                        else if (key4=="threads")
                        {
                            thread=val5;
                        }
                        else   if (key4=="cpu")
                        {
                            cpup = val5;
                        }
                        else   if (key4=="threads")
                        {
                            threads = val4;
                        } else if (key4=="net"){
                            window.console &&   console.log("json net data ");
                            //network info
                            $.each(val5, function(netK, netV){
                                //netK should be the nic name its not
                                $.each(netV, function(k6, v6){
                                    //k6 = nic id
                                    window.console &&   console.log("json disk data " + k6);
                                    $.each(v6, function (k7, v7){
                                        var read=null;
                                        var write=null;
                                        $.each(v7, function (k8,v8){
                                            if (k8=="TX")
                                                read = Number(v8);
                                            if (k8 == "RX")
                                                write=Number(v8);
                                        });
                                        if (read!=null && write!=null)
                                        {
                                            
                                            /******************** INSERTION *******************/
                                            
                                            var existingItem = getArrayItem(key1 + ' '  + k6 + " TX",net);
                                            if (existingItem==null)
                                            {
                                                var t=new Object();
                                                t.id = key1 + ' '  + k6 + " TX";
                                                t.points = [];
                                                //var idx=net.push(k6 + " TX", t);
                                                t.points.push({
                                                    x:ts, 
                                                    y:write, 
                                                    policyurl:key1
                                                });
                                                net.data.push(t);
                                            //net[k6 + "TX"].data = [];
                                            //net[k6 + "TX"].data.push(t);
                                            }else
                                            {
                                                existingItem.push({
                                                    x:ts, 
                                                    y:write, 
                                                    policyurl:key1
                                                });
                                            
                                            }
                                            /******************** INSERTION *******************/
                                            existingItem = getArrayItem(key1 + ' '  + k6 + " RX",net);
                                            if (existingItem==null)
                                            {
                                                var t=new Object();
                                                t.id=key1 + ' '  + k6 + " RX";
                                                t.points=[];
                                                t.points.push({
                                                    x:ts, 
                                                    y:read, 
                                                    policyurl:key1
                                                });
                                                net.data.push(t);
                                            }
                                            else
                                            {
                                                existingItem.push({
                                                    x:ts, 
                                                    y:read, 
                                                    policyurl:key1
                                                });
                                            }
                                        }
                                    });
                                    
                                });
                                

                            });
                        }
                        else if (key4=="disk")
                        {
                            window.console &&   console.log("json disk data found ");
                            $.each(val5, function(diskK, diskV){
                                //diskK should be the partition/drive name
                                
                                $.each(diskV, function(k6, v6){
                                    //k6 is the partition name
                                    window.console &&   console.log("json disk data " + k6);
                                    $.each(v6, function(k7,v7){
                                        var freespacep=null;
                                        var read=null;
                                        var write=null;
                                        $.each(v7, function(k8,v8){
                                            if (k8=="FreeSpacePercent")
                                                freespacep = 100 - Number(v8);
                                            if (k8=="R")
                                                read = Number(v8);
                                            if (k8 == "W")
                                                write=Number(v8);
                                        });
                                        
                                        /******************** INSERTION *******************/
                                        
                                        if (read!=null && write!=null)
                                        {
                                            var existingItem = getArrayItem(key1 + ' '  +k6 + " Write", disk);
                                            if (existingItem==null)
                                            {
                                                var t=new Object();
                                                t.id =key1 + ' '  + k6 + " Write";
                                                t.points=[];
                                                t.points.push({
                                                    x:ts, 
                                                    y:write, 
                                                    policyurl:key1
                                                });
                                                disk.data.push(t);
                                            //                                                freediskspace[k6 + " Write"].data = [];
                                            //                                            freediskspace[k6 + " Write"].data.push(t);
                                            //freediskspace.push(k6 + " Write", t);
                                            }else
                                                existingItem.push({
                                                    x:ts, 
                                                    y:write, 
                                                    policyurl:key1
                                                });
                                        
                                            /******************** INSERTION *******************/
                                            existingItem = getArrayItem(key1 + ' '  + k6 + " Read",disk);
                                            if (existingItem==null)
                                            {
                                                var t=new Object();
                                                t.id = key1 + ' '  + k6 + " Read";
                                                t.points=[];
                                                t.points.push({
                                                    x:ts, 
                                                    y:read, 
                                                    policyurl:key1
                                                });
                                                //freediskspace.push(k6+ " Read", t);
                                                //freediskspace[k6 + " Read"].data = [];
                                                //freediskspace[k6 + " Read"].data.push(t);
                                                disk.data.push(t);
                                            }
                                            else
                                            {
                                                existingItem.push({
                                                    x:ts, 
                                                    y:read, 
                                                    policyurl:key1
                                                });
                                            }
                                        }
                                        
                                        if (freespacep != null)
                                        {
                                            
                                            /******************** INSERTION *******************/
                                            var existingItem = getArrayItem(key1 + ' '  + k6 + '%',freediskspace);
                                            if (existingItem==null)
                                            {
                                                var t=new Object();
                                                t.id=key1 + ' '  + k6 + '%';
                                                t.points=[];
                                                t.points.push({
                                                    x:ts, 
                                                    y:freespacep, 
                                                    policyurl:key1
                                                });
                                                freediskspace.data.push(t);
                                            //freediskspace[k6].data = [];
                                            //freediskspace[k6].data.push(t);
                                            }else
                                            {
                                                existingItem.push({
                                                    x:ts, 
                                                    y:freespacep, 
                                                    policyurl:key1
                                                });
                                            }
                                        }
                                    });
                                    
                                //FreeSpace (%)
                                //R
                                //W
                                });
                                
                            });
                        //disk info
                        }
                    });
                    
                    window.console &&   console.log('loading json machine data cpu '+ cpup + ' thread' + thread + ' mem' + memp + ' at ' + key3 + ' ' + ts);
                    cpu.push({
                        x:ts, 
                        y:cpup, 
                        policyurl:key1
                    });
                    
                    threads.push({
                        x:ts, 
                        y:thread, 
                        policyurl:key1
                    });
                    
                    mem.push({
                        x:ts, 
                        y:memp, 
                        policyurl:key1
                    });
                    
                    
                });
                
                
            });
        });
        
        window.console &&   console.log("dedupe mem");
        mem.dedupe({
            sortComparator: sortfunction, 
            dedupeComparator:compareFunction
        });
        window.console &&   console.log("dedupe cpu");
        cpu.dedupe({
            sortComparator: sortfunction, 
            dedupeComparator:compareFunction
        });
        window.console &&   console.log("dedupe threads");
        threads.dedupe({
            sortComparator: sortfunction, 
            dedupeComparator:compareFunction
        });
        window.console &&   console.log("dedupe free disk space");
        for (i=0; i < freediskspace.data.length; i++){
            freediskspace.data[i].points.dedupe({
                sortComparator: sortfunction, 
                dedupeComparator:compareFunction
            });
            window.console &&   console.log("disk space chart for " + freediskspace.data[i].id);
            createMachineChart(freediskspace.data[i].points, freediskspace.data[i].id, percentage, 'Resource Utilization %','percentscale');
        }
        window.console &&   console.log("dedupe net i/o");
        for (i=0; i < net.data.length; i++){
            net.data[i].points.dedupe({
                sortComparator: sortfunction, 
                dedupeComparator:compareFunction
            });
            window.console &&   console.log("net i/o chart for " + net.data[i].id);
            createMachineChart(net.data[i].points, net.data[i].id, iochart, 'I/O KB/s','networkdiskchart');
        }
        window.console &&   console.log("dedupe disk i/o");
        for (i=0; i < disk.data.length; i++){
            disk.data[i].points.dedupe({
                sortComparator: sortfunction, 
                dedupeComparator:compareFunction
            });
            window.console &&   console.log("disk i/o chart for " + disk.data[i].id);
            createMachineChart(disk.data[i].points, disk.data[i].id,iochart, 'I/O KB/s','networkdiskchart');
        }

        window.console &&   console.log("creating mem cpu and thread charts");
        createMachineChart(mem, url+' ' +  "Memory%", percentage, 'Resource Utilization %','percentscale');
        createMachineChart(cpu,  url+' ' + "CPU%", percentage, 'Resource Utilization %','percentscale');
        createMachineChart(threads,  url+' ' + "Threads", counters, 'Resource Counters','threadsfilecharts');
    //      networkdiskchart
    // percentscale
    //* threadsfilecharts
    
    });
        
    

}

/*
 expecting a data objects containing
    .data array of 
        .id=some key
        .points = array of points
returns the array if found

 */
function getArrayItem(key, data)
{
    window.console &&   console.log("checking data for " + key );
    for (i=0; i < data.data.length; i++)
    {
        if (data.data[i].id == key)
        {
            window.console &&   console.log("key found " + key );
            return data.data[i].points;
        }
    }
    window.console &&   console.log("key NOT found " + key );
    return null;
}

var percentscale;
var networkdiskchart;
var threadsfilecharts;

/**
 *networkdiskchart
 * percentscale
 * threadsfilecharts
 */
function createMachineChart(data, seriesname, renderto, title, charttype)
{
    var chartvar=null;
    var yaxis=null;
    if (charttype=='percentscale')
    {
        yaxis= [{
            min: 0,
            max : 100,
            title: {
                text:'Percent In Use'
            }
        }];
        if (percentscale!=null)
        {
            window.console &&   console.log("the chart " + renderto + " exists, adding series " + seriesname);
            //check if the series exists already, if so, just add the points and rerender
            var idx=-1
            var allseries = percentscale.series;
            window.console && 
            console.log(allseries.length + ' series exist');
            $.each(allseries, function(ds)
            {
                if (allseries[ds].name == seriesname )
                {
                    idx = ds;
                }
            });
            if (idx >= 0)
            {
                //series exists, just add the data points
                window.console &&   console.log("the series  " + seriesname + ' exists on ' + renderto + "  adding the new data points");
                $.each(data, function(x){
                    allseries[idx].addPoint(data[x], false, true);
                });
                percentscale.redraw();
                return;
            }
            else{
                window.console &&   console.log("the series  " + seriesname + ' does NOT exists on ' + renderto + "  adding the new series");
                percentscale.addSeries({
                    data: data, 
                    name:seriesname,
                    tooltip: {
                        valueDecimals: 1
                    }
                }, true, true);
                return;
            }
        }
    }
    if (charttype=='networkdiskchart')
    {
        yaxis= [{
            min: 0,
            title: {
                text:'Rate KB/sec'
            }
        }];
        
        if (networkdiskchart!=null)
        {
            window.console &&   console.log("the chart " + renderto + " exists, adding series " + seriesname);
            //check if the series exists already, if so, just add the points and rerender
            var idx=-1
            var allseries = networkdiskchart.series;
            window.console && 
            console.log(allseries.length + ' series exist');
            $.each(allseries, function(ds)
            {
                if (allseries[ds].name == seriesname )
                {
                    idx = ds;
                }
            });
            if (idx >= 0)
            {
                window.console &&   console.log("the series  " + seriesname + ' exists on ' + renderto + "  adding the new data points");
                //series exists, just add the data points
                $.each(data, function(x){
                    allseries[idx].addPoint(data[x], false, true);
                });
                networkdiskchart.redraw();
                return;
            }
            else{
                window.console &&   console.log("the series  " + seriesname + ' does NOT exists on ' + renderto + "  adding the new series");
                networkdiskchart.addSeries({
                    data: data, 
                    name:seriesname,
                    tooltip: {
                        valueDecimals: 1
                    }
                }, true, true);
                return;
            }
        }
    }
    if (charttype=='threadsfilecharts')
    {
        yaxis= [{
            min: 0,
            title: {
                text:'Count'
            }
        }];

        if (threadsfilecharts!=null)
        {
            window.console &&   console.log("the chart " + renderto + " exists, adding series " + seriesname);
            //check if the series exists already, if so, just add the points and rerender
            var idx=-1
            var allseries = threadsfilecharts.series;
            window.console && 
            console.log(allseries.length + ' series exist');
            $.each(allseries, function(ds)
            {
                if (allseries[ds].name == seriesname )
                {
                    idx = ds;
                }
            });
            if (idx >= 0)
            {
                window.console &&   console.log("the series  " + seriesname + ' exists on ' + renderto + "  adding the new data points");
                //series exists, just add the data points
                $.each(data, function(x){
                    allseries[idx].addPoint(data[x], false, true);
                });
                threadsfilecharts.redraw();
                return;
            }
            else{
                window.console &&   console.log("the series  " + seriesname + ' does NOT exists on ' + renderto + "  adding the new series");
                threadsfilecharts.addSeries({
                    data: data, 
                    name:seriesname,
                    tooltip: {
                        valueDecimals: 1
                    }
                }, true, true);
                return;
            }
        }
    }
    
    chartvar = new Highcharts.StockChart({
        chart : {
            renderTo : renderto,
            type: 'spline'
									
        },
        plotOptions: {
            series: {
                cursor: 'pointer'
            }
        },
        rangeSelector : {
            selected : 1,
            buttons: [
            {
                type: 'minute',
                count: 5,
                text: '5m'
            }, 
            {
                type: 'minute',
                count: 15,
                text: '15m'
            },
									   
            {
                type: 'hour',
                count: 1,
                text: '1hr'
            },
            {
                type: 'day',
                count: 1,
                text: '1d'
            }]
        },
        marker : {
            enabled : true,
            radius : 3
        },
        yAxis: yaxis,
        title : {
            text : title
        },
				
        tooltip : {
            valueDecimals : 1
        },
        series : [{
            name : seriesname,
            url: seriesname,
            data : data,
            tooltip: {
                valueDecimals: 1
            }
        }
        ]
    });
    if (charttype == 'percentscale')
        percentscale = chartvar;
    if (charttype == 'networkdiskchart')
        networkdiskchart = chartvar;
    if (charttype == 'threadsfilecharts')
        threadsfilecharts = chartvar;
        
}




function createBrokerCharts(url, renderto,since )
{
    //this will get the current broker data
    $.getJSON('reporting/brokerDataJson.jsp?uri=' + url , function(data)
    {
        window.console &&   console.log('loading json broker data success '+ 'reporting/machineDataJson.jsp?uri=' + url );
        
        var ts=since;
        var dataset=[];
        var maxdepth=0;
        $.each(data, function(key1, val2) {
            //first node is the url key1 url to the broker
            $.each(val2, function(key2, val3) {
                $.each(val3, function(key3, val4) {
                    var activeconsumers=0;
                    var totalconsumers=0;
                    var bytesin=0;
                    var bytesout=0;
                    var bytesdropped=0;
                    var msgin=0;
                    var msgout=0;
                    var msgdropped=0;
                    
                    //key3 is the topic name
                    
                    $.each(val4, function(key4, val5) {
                        //each for value 
                        if (key4=="depth")
                        {
                            var x = Number(val5);
                            if (x > maxdepth)
                                maxdepth = x;
                        }
                        else if (key4=="timestamp")
                        {
                        //        ts=val5;
                        }
                    });
                });
            });
        });
        window.console &&   console.log('loading json broker data cpu '+ url + ' depth' + maxdepth + ' ' + ts );
        dataset.push({
            x:ts, 
            y:maxdepth, 
            policyurl:url
        });
 
        window.console &&   console.log("creating broker charts");

        createMachineChart(dataset, url + ' ' + "Max Depth", renderto, 'Resource Counters','threadsfilecharts');
        
    });
}
