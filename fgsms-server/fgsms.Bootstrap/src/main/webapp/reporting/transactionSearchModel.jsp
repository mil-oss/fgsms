<div id="myModalTransactionSearch" class="modal fade in" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="false" style="display: block; display:none; ">
    <div class="modal-header">
        <a href="#" class="close" data-dismiss="modal" aria-hidden="true">×</a>
        <h3 id="myModalLabel">Transaction Search</h3>
    </div>
    <div class="modal-body">
        <h4>Transaction ID</h4>
        <input type="text" id="tid" name="tid" value="" placeholder="Transaction ID"/> <a href="javascript:goSearch();" class="btn btn-primary">Search</a><br>
        <div id="modalpayload2"></div>
    </div>
    <div class="modal-footer">
        <button class="btn" data-dismiss="modal">Close</button>
    </div>
</div>
<script type="text/javascript">  
    /**
     * loads a new model, content is loaded from another jsp that fetches a specification transaction log and payloads
     */
    function goSearch()
    {
        var id = $("#tid").val();
        $("#modalpayload2").html('Loading...');
        loadpage('reporting/SpecificTransactionLogViewer.jsp?ID=' + id, 'modalpayload2');
    }
    </script>