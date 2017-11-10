<div id="myModal" class="modal fade in" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="false" style="display: block; display:none; ">
    <div class="modal-header">
        <a href="#" class="close" data-dismiss="modal" aria-hidden="true">×</a>
        <h3 id="myModalLabel">Transaction Details</h3>
    </div>
    <div class="modal-body">
        <h4>Transaction ID</h4>
        <p id="tid"></p>

        <h4>Status</h4>
        <p id="successfail"></p>

        <h4>SLA Message</h4>
        <p id="slafault"></p>

        <h4>Consumer</h4>
        <p id="consumer"></p>

        <h4>Request URL</h4>
        <p id="requesturl"></p>

        <h4>Observed at</h4>
        <p id="recordedat"></p>

        <h4>Policy URL</h4>
        <p id="policyurl"></p>

        <h4>Action or HTTP Method</h4>
        <p id="action"></p>

        <h4>Timestamp</h4>
        <p id="timestamp"></p>

        <h4>Response Time</h4>
        <p id="responsetime"></p>
 
        <h4>Request Size</h4>
        <p id="requestsize"></p>

        <h4>Response Size</h4>
        <p id="responsesize"></p>

        <h4>Payloads Available</h4>
        <p><a href="#" class="tooltip-test" data-original-title="Tooltip" id="requestpayloadavailable"></a> <a href="#" class="tooltip-test" data-original-title="Tooltip" id="responsepayloadavailable"></a></p>
        <a href="javascript:goModal();" class="btn btn-primary">View</a>

    </div>
    <div class="modal-footer">
        <button class="btn" data-dismiss="modal">Close</button>
    </div>
</div>
<script type="text/javascript">  
    /**
     * loads a new model, content is loaded from another jsp that fetches a specification transaction log and payloads
     */
    function goModal()
    {
        var id = $("#tid").text();
        $('#myModalDetails').modal();
        $('#myModalDetails').resizable();
        loadpage('reporting/SpecificTransactionLogViewer.jsp?ID=' + id, 'modalpayload');
    }
    </script>
<div id="myModalDetails" class="modal fade in" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="false" style="position:fixed; display: block; display:none; width:80%; height: 80%; left:25%; ">
    <div class="modal-header">
        <a href="#" class="close" data-dismiss="modal" aria-hidden="true">×</a>
        <h3 id="myModalLabel">Transaction Details</h3>
    </div>
    <div class="modal-body" id="modalpayload">

    </div>
    <div class="modal-footer">
        <button class="btn" data-dismiss="modal">Close</button>
    </div>
</div>
