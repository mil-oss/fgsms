/* 
 * This file contains all of the javascript code used for the bread crumbs
 */


var baseArray = new Array(); 
baseArray['home.jsp'] = 'Home'; 
baseArray['status.jsp'] = 'My Services'; 
baseArray['perf.jsp'] = 'Performance';  
baseArray['infrastructure.jsp'] = 'Infrastructure';  
baseArray['data.jsp'] = 'Data';  
baseArray['federation.jsp'] = 'Federation';  
baseArray['admin.jsp'] = 'Administration';  
baseArray['reporting/scheduledReportsMain.jsp'] = 'Scheduled Reports'; 
baseArray['reporting/transactionlogs.jsp'] = 'Transaction Logs';
baseArray['reporting/reportGenerator.jsp'] = 'Report Generator';
baseArray['reporting/dataExport.jsp'] = 'Data Export'; 
baseArray['reporting/scheduledMainPost.jsp'] = 'Add Scheduled Reporting Job'; 
baseArray['reporting/scheduledEditPost.jsp'] = 'Scheduled Reporting Edit Job'; 
baseArray['reporting/scheduledReportsMainEdit.jsp'] = 'Scheduled Reporting Edit Job'; 
baseArray['reporting/SpecificTransactionLogViewer.jsp'] = 'Web Service Transaction Viewer'; 

//home
baseArray['profile.jsp'] = 'Service Profiles'; 
baseArray['contact.jsp'] = 'Contact Us'; 
baseArray['requestAccess.jsp'] = 'Request Access'; 

//infrastructure
baseArray['brokers/messageBrokers.jsp'] = 'Message Brokers'; 
baseArray['os/domains.jsp'] = 'Domains and Servers'; 
baseArray['os/servers.jsp'] = 'Servers'; 
baseArray['os/serverprocess.jsp'] = 'Server and Process'; 

//brokers
baseArray['brokers/messageBrokerDetail.jsp'] = 'Broker Detail'; 
baseArray['brokers/messageBrokerProfile.jsp'] = 'Broker Profile'; 
baseArray['brokers/messageBrokerHistory.jsp'] = 'Broker History'; 



baseArray['federation/endpointPublisher.jsp'] = 'EndPoint Publisher'; 
baseArray['federation/uddibrowser.jsp'] = 'UDDI Browser'; 


baseArray['addservice.jsp'] = 'Add A Service'; 
baseArray['admin/globalPolicies.jsp'] = 'Global Policies'; 
baseArray['admin/globalSettings.jsp'] = 'Global Settings'; 
baseArray['admin/mailSettings.jsp'] = 'Mail Settings'; 
baseArray['admin/index.jsp'] = 'Administration'; 
baseArray['admin/siteAdministration.jsp'] = 'Site Admin'; 
baseArray['admin/siteAgents.jsp'] = 'Site Agents'; 
baseArray['admin/securityLevel.jsp'] = 'Security Level'; 
baseArray['admin/audit.jsp'] = 'Audit'; 
baseArray['admin/removeStaleRecords.jsp'] = 'Remove Stale Records'; 
baseArray['admin/dataCollectors.jsp'] = 'Data Collectors'; 
baseArray['admin/serviceHosts.jsp'] = 'Service Hosts'; 
baseArray['admin/agents.jsp'] = 'Agents'; 
baseArray['profile/policyImport.jsp'] = 'Policy Imports'; 

baseArray['help/services.jsp'] = 'Help Services'; 
baseArray['help/index.jsp'] = 'Help';  
baseArray['help/about.jsp'] = 'About FGSMS'; 
baseArray['help/sdk.jsp'] = 'Help SDK'; 
baseArray['help/agentconfig.jsp'] = 'Help Agent Config'; 
baseArray['help/uddi.jsp'] = 'Help UDDI'; 
baseArray['help/fds.jsp'] = 'Help FDS'; 
baseArray['help/policies.jsp'] = 'Help Policies'; 
baseArray['help/sla.jsp'] = 'Help SLA'; 
baseArray['help/agent.jsp'] = 'Help Agent'; 
baseArray['help/versions.jsp'] = 'Help Versions'; 
baseArray['help/permissions.jsp'] = 'Help Permissions'; 
baseArray['help/status.jsp'] = 'Help Status'; 
baseArray['help/qpid.jsp'] = 'Help Qpid'; 
baseArray['help/hornetq.jsp'] = 'Help HornetQ'; 
baseArray['help/amqp.jsp'] = 'Help AMQP'; 
baseArray['help/smx.jsp'] = 'Help SMX'; 
baseArray['help/mrg.jsp'] = 'Help MRG'; 

baseArray['help/alerting.jsp'] = 'Help Alerting'; 
baseArray['help/data.jsp'] = 'Help Data'; 
baseArray['help/bueller.jsp'] = 'Help Bueller'; 
baseArray['help/email.jsp'] = 'Help Email'; 
baseArray['help/healthstatuscheck.jsp'] = 'Help Health Status Check'; 
baseArray['help/loggers.jsp'] = 'Help Loggers'; 
baseArray['help/wsn.jsp'] = 'Help WS-Notification'; 
baseArray['help/scheduledreports.jsp'] = 'Help Reporting'; 

baseArray['profile/permission.jsp'] = 'Profile Permission'; 




function updateNav(id){
    //   alert("id: "+id); 
    var revisedId = id.replace(/\s/g, ""); 
    var foundLi =  $('ul.nav').find('li#'+revisedId); 
    if(foundLi.length!=0){
        $('ul.nav li').removeClass('active'); 
        foundLi.addClass('active');
    }
}



function clearAllLi(){
    $('ul.breadcrumb li').removeClass('active').empty(); 
    $('.removeAfter').remove(); 
}

    
function breadcrumbing(link, parent){
    try{
        var index2 = link.indexOf(".jsp"); 
        link = link.substring(0, index2+4); 
        var first = $('ul.breadcrumb li#first'); 
        var second = $('ul.breadcrumb li#second:first'); 
        var third = $('ul.breadcrumb li#third'); 
        if(link=="home.jsp"){
            clearAllLi(); 
            first.text('Home').addClass('active'); 
            $('div.nav-collapse ul li').removeClass('active'); 
            updateNav('Home'); 
        }
        else{
            var linkThing; 
            var parentThing; 
            var parentIndex; 
            var parentLink; 
            linkThing = baseArray[link]; 
            if (linkThing!=null){
                clearAllLi(); 
                first.append("<a href=\"javascript:loadpage('home.jsp','mainpane');\">Home</a><span class=\"divider\">/</span>"); 
                if (parent!=null){
                    var eachPar = parent.split("&"); 
                            
                    var i; 
                    for (i=0; i<eachPar.length; i++){
                        //alert("eachPar: "+eachPar[i]); 
                        var history = ""; 
                        if(i>0){
                            var index; 
                            for (index=0; index<i; index++){
                                if (index!=0)
                                    history = history.concat("&"); 
                                history = history.concat(eachPar[index]); 
                            }
                                        
                        }
                    
                  
                        parentIndex = eachPar[i].indexOf(".jsp"); 
                        parentLink = eachPar[i].substring(0, parentIndex+4); 
                        parentThing = baseArray[parentLink]; 
                                
                        if (history!="")
                            second.clone().append("<a href=\"javascript:loadpage('"+eachPar[i]+"','mainpane', '"+history+"');\">"+parentThing+"</a><span class=\"divider\">/</span>").addClass('removeAfter').insertBefore(third);
                        else 
                            second.clone().append("<a href=\"javascript:loadpage('"+eachPar[i]+"','mainpane');\">"+parentThing+"</a><span class=\"divider\">/</span>").addClass('removeAfter').insertBefore(third);
                    }
                    third.text(linkThing).addClass('active'); 
                    updateNav(parentThing); 
                                
                }
                else{
                    second.text(linkThing).addClass('active'); 
                    updateNav(linkThing); 
                }

            }else     window.console &&   console.log("error the link " + link + ' hasnt been defined in the breadcrumb array'); 

        }
    }
    catch (e){
        window.console &&   console.log("unhandled exception caught " + link + ' ' + e); 
    }
}
