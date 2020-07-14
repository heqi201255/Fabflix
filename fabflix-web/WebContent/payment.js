let form = $("#paymentForm")
let template = $(
    "<div id='order_detail' class='row justify-content-center'>\n" +
    "      <table>\n" +
    "       <thead>\n" +
    "        <tr>\n" +
    "          <th>Sale IDs:&nbsp;</th>\n" +
    "          <td id = 'sale'></td>" +
    "          <th>&nbsp;&nbsp;&nbsp;&nbsp;Title:&nbsp;</th>\n"  +
    "          <td id = 'title'></td>\n" +
    "          <th>&nbsp;&nbsp;&nbsp;&nbsp;Quantity:&nbsp;</th>\n"  +
    "          <td id = 'quantity'></td>\n" +
    "        </tr>\n" +
    "       </thead>" +
    "      </table>\n" +
    "</div>")
console.log(form);
let q = '';

function submitPaymentForm() {
    var cid = document.getElementById('cnumber').value;
    console.log(cid);
    var fname = document.getElementById('fname').value;
    var lname = document.getElementById('lname').value;
    var edate = document.getElementById('edate').value;
    console.log(fname);
    console.log(lname);
    console.log(edate);
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    //formSubmitEvent
    //formSubmitEvent.preventDefault();
    //form.submit();
    $.ajax("api/payment-auth", {
        method: "POST",
        data: {
            card: cid,
            first: fname,
            last: lname,
            exp: edate,
        },
        success: handleConfirm
    })
}

function addtitle(mid, rd) {
    $("#order_detail_"+mid).find("#title_"+mid).text(rd["title"])
}
function handleConfirm(resultData){
    console.log(resultData);
    if (resultData["status"]==="success"){
        console.log("success");
        $.ajax("api/payment", {
            method: "POST",
            data: q,
            success: function (resultData) {
                let confirmPage = $('#confirm');
                for (let i=0; i<resultData.length;i++){
                    let mid = resultData[i]["movieId"].substring(5);
                    let sales = resultData[i]["saleId"];
                    let table = template.clone();
                    table.find("#sale").attr("id","sale_"+mid);
                    table.find("#title").attr("id","title_"+mid);
                    table.find("#quantity").attr("id","quantity_"+mid);
                    table.attr('id', 'order_detail_'+mid);
                    table.find("#sale_"+mid).text(sales);
                    table.find("#quantity_"+mid).text(resultData[i]["quantity"])
                    confirmPage.append(table);
                    $.ajax({
                        dataType: "json",
                        method: "GET",
                        url: "api/cart?id="+mid,
                        success: (resultData) => addtitle(mid,resultData)
                    });
                }
            }
        })
        $("#payBody").remove();
        $("#confirm").text("Thank you for shopping!");
        $('#message').text("");
    } else {
        console.log("fail: "+resultData["message"]);
        $('#message').text(resultData["message"]);
    }
}

$(function () {
    let total = 0;
    for (let i=0; i<sessionStorage.length;i++){
        if (sessionStorage.key(i).startsWith("cart_")){
            let val = sessionStorage.getItem(sessionStorage.key(i)).split("_")
            total = total + val[0]*val[1]
            q = q + '&' + sessionStorage.key(i) + '=' + val[0].toString();
        }
    }
    $('#total').text("Total Price: "+total.toString())
})
