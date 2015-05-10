<script>
    function alertFunc() {
    <%
        String errMsg = request.getParameter("errMsg");
        String goodMsg = request.getParameter("goodMsg");

        if ((errMsg != null) && (goodMsg == null)) {
            if (!errMsg.equals("")) {
    %>
        new PNotify({
            title: 'Error',
            text: '<%=errMsg%>',
            type: 'error'
        });
    <%
        }
    } else if ((errMsg == null && goodMsg != null)) {
        if (!goodMsg.equals("")) {
    %>
        new PNotify({
            title: 'Success',
            text: '<%=goodMsg%>',
            type: 'success'
        });
    <%
            }
        }
    %>
    }
</script>