/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */




$(document).ready(function(){
   var addAlertsButton = $("input[type='button']#addingAlerts"); 
    var delAlertsButton = $("input[type='button']#deletingAlerts"); 
    var cancelAlertsButton =  $("input[type='button']#cancelAlerts"); 
    
    $('tr.slatemplate').hide(); 
    $('p#alertChangeMessage').hide(); 
    cancelAlertsButton.hide(); 
    $('td.delRadioButton').hide(); 
   
    //in order to remove the name-attribute of the non-adding sla content
    $('tr.slarecords input, tr.slarecords select').attr("disabled", true).removeAttr("name"); 
    


    
    //edit: adding alerts
    addAlertsButton.click(function(){
       // $('div.slatemplate').clone().removeClass('slatemplate').addClass('slarecords').show().appendTo('div#slacontainer'); 
        $(this).hide(); 
        delAlertsButton.hide(); 
         $('p#alertChangeMessage').show(); 
         $('tr.slatemplate').show(); 
         cancelAlertsButton.show(); 
   
       
        }); 
        
        function deleteAlertMode(){
           //$('table#slacontainer').attr("frame", "box"); 
           $('td.delRadioButton input').attr("disabled", false).attr("name", "delAlertId"); 
           $('td.delRadioButton').show(); 
            }
        
        function normalAlertMode(){
           // $('table#slacontainer').attr("frame", ""); 
           $('td.delRadioButton input').attr("disabled", true).removeAttr("name"); 
            $('td.delRadioButton').hide(); 
            }
        
        
        //edit: deleting alerts
   delAlertsButton.click(function(){
        $(this).hide(); 
       addAlertsButton.hide(); 
         $('p#alertChangeMessage').show(); 
         deleteAlertMode(); 
        cancelAlertsButton.show(); 
 
   
       
        }); 
        
        
      //edit: cancel alerts
      cancelAlertsButton.click(function(){
           normalAlertMode(); 
          $(this).hide(); 
          delAlertsButton.show(); 
         addAlertsButton.show();
          $('p#alertChangeMessage').hide(); 
          $('tr.slatemplate').hide(); 
          //reset the value in the template
          $('tr.slatemplate input, tr.slatemplate select').attr("name", "");
         
               
          }); 
        

    
    //edit: logto 
          var logTo = $('input#notificationType').val(); 
      if (logTo!=null){
          $("select[name='LogTo']").val(logTo).attr("selected", true); 
          }
    

    $('tr.slarecords').each(function(){
       // console.log($(this)); 
       var notifType = $(this).find('td input#notificationType').val(); 
       //console.log("notifType: "+notifType); 
       $(this).find('td select#Action').val(notifType).select(); 
       var selected = $(this).find('td select#Action option:selected').attr("id"); 
       showSLADetails($(this), selected);
        }); 
        


    $('select#Action').on("change", function(){
        var selected = $(this).find('option:selected').attr("id"); //$('select#Action option:selected').attr("id"); 
        showSLADetails($(this).parent().parent().parent(), selected); 
      
        }); 
        
        function showSLADetails(origin, selected){
          origin.find('div#sladetails div').each(function(){
            if($(this).attr('id')!=selected)
                $(this).css('display', "none"); 
            else 
            $(this).css('display', "");
        }); 
            }

  
    
   $('input[name="exporttype"], input[name="recordstype"]').change(function(){
               checkIfAudit(); 
               }); 
           
           /**
           $('input[name="recordstype"]').change(function(){
               checkIfAudit(); 
               }); **/
               

    
    checkIfAudit(); 

         $( "#accordion" ).accordion({
            autoHeight: false,
            navigation: true
        });
    $("#recordstype").buttonset();
    $("#exporttype").buttonset();
    $("#ScheduleType").buttonset();
    $("#joiner").buttonset();
    $( "input:submit").button();
    $( "input:button").button();
    $( "#untiltime").resizable();
    $( "#fromtime").resizable();
    $( "#additionalusers").resizable();

   
   /*
        if($('#openlisting').is(':visible')){
            alert("reload"); 
            var x = document.getElementById('openlisting');
            x.style.display = "none";
            }else{
                alert("don't reload"); 
                }
        });**/
    
    $('input[name="exporttype"]').on('click', function(){
        if($('#HTML').is(':checked')){
            toggleVisiblityLocal(false); 
            }else{
                toggleVisiblityLocal(true); 
                }
        });
        
           function checkIfAudit(){
        if($("input#HTML[name='exporttype']").is(":checked")){
           document.getElementById('auditselect').style.display="none"; 
            document.getElementById('services').style.display = ""; 
            }else{
            if($('#AuditLogs').is(':checked')){
            document.getElementById('auditselect').style.display=""; 
            document.getElementById('services').style.display = "none"; 
            }else{
            document.getElementById('auditselect').style.display="none"; 
            document.getElementById('services').style.display = ""; 
                }
           }
           }

   function  toggleVisiblityLocal(obj)
    {
        if (obj==false)
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
   
 

 
 
    

   
    

   $("#recordstype").click(function(){ 
  if($('#AuditLogs').is(':checked')){
       document.getElementById('auditselect').style.display=""; 
       document.getElementById('services').style.display = "none"; 
       }
       }); 
       

   
    });