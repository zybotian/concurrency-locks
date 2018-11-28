function startPurchaseV1() {
    var num = 100;
    for (var i = 1; i <= num; i++) {
         $.ajax({
               type:"POST",
               url:"product/purchase/v1",
               dataType:"json",
               data:{userId:i, productId:2018112815220001},
               success:function(result) {
                   $("#txt_output").append(result.data+": "+result.message + "\n");
               },
               error:function(jqXHR){
                   $("#txt_output").append("发生错误:"+jqXHR.status+"\n\n");
               }
           });
    }
}

function startPurchaseV2() {
    var num = 100;
        for (var i = 1; i <= num; i++) {
             $.ajax({
                   type:"POST",
                   url:"product/purchase/v2",
                   dataType:"json",
                   data:{userId:i, productId:2018112815220002},
                   success:function(result) {
                       $("#txt_output").append(result.data+": "+result.message + "\n");
                   },
                   error:function(jqXHR){
                       $("#txt_output").append("发生错误:"+jqXHR.status+"\n\n");
                   }
               });
        }
}