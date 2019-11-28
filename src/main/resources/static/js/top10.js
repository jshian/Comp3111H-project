$(document).ready(function () {
    $.ajax({
        type : "GET",
        contentType: "application/json;charset=UTF-8",
        url : "/players/find_score_top10",
        success : function(result) {
            console.log(result);
            var content="";
            for (var i =0;i<result.length;i++){
                content+="<tr>";
                content+="<td>"+(i+1)+"</td>";
                content+="<td>"+result[i].name+"</td>"
                content+="<td>"+result[i].score+"</td>"
                content+="</tr>"
            }
            $("#table_top10").html(content)
        },
        error : function(e){
            console.log(e.status);
            console.log(e.responseText);
        }
    });
})