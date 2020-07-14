
let template = $(
    "<div class='row justify-content-center'>\n" +
    "      <table>\n" +
    "       <thead>\n" +
    "        <tr>\n" +
    "          <th>Title:&nbsp;</th>\n" +
    "          <td id = 'title'>Title</td>" +
    "          <th>&nbsp;&nbsp;&nbsp;&nbsp;Price:&nbsp;</th>\n"  +
    "          <td id = 'price'>Price</td>\n" +
    "          <th>&nbsp;&nbsp;&nbsp;&nbsp;Quantity:&nbsp;</th>\n"  +
    "          <td id = 'quantity'>Quantity</td>\n" +
    "          <th>&nbsp;</th>\n" +
    "          <td><button id='b_add'>+</button></td>\n" +
    "          <td><button id='b_minus'>-</button></td>\n" +
    "          <td><button id='b_remove'>Remove</button></td>\n" +
    "        </tr>\n" +
    "       </thead>" +
    "      </table>\n" +
    "</div>"
    // '<div class="row justify-content-center">\n' +
    // '        <div id="title"></div>\n' +
    // '        <div>        Price: </div>\n' +
    // '        <div id="price"></div>\n' +
    // '        <div>        Quantity: </div>\n' +
    // '        <div id="quantity"></div>\n' +
    // '        <div>        </div>\n' +
    // '        <button id="b_add">+</button>\n' +
    // '        <button id="b_minus">-</button>\n' +
    // '        <button id="b_remove">Remove</button>\n' +
    // '    </div>'
)

function addTitle(movie_id, resultData){
    $("#"+movie_id).find("#title_"+movie_id).text(resultData["title"]);
}

$(function () {
    let total = 0;
    $.ajax({
        dataType: "json",
        method: "GET",
        url: "api/cart",
        success: handleCartResult
    })
    let cart = $("#cart_body");
    if (sessionStorage.getItem("cartNum")==="0"){
        $('#change_message').text("Empty Cart");
        $('#payment').attr('onclick', '');
    } else {
        for (let i=0; i<sessionStorage.length; i++){
            if(sessionStorage.key(i).startsWith("cart_")){
                let m = template.clone();
                let movie_id = sessionStorage.key(i).split("_")[1];
                let movie_quantity = sessionStorage.getItem(sessionStorage.key(i)).split("_")[0];
                let movie_price = sessionStorage.getItem(sessionStorage.key(i)).split("_")[1];
                total = total+ movie_price*movie_quantity;
                m.attr('id', movie_id);
                $.ajax({
                    dataType: "json",
                    method: "GET",
                    url: "api/cart?id="+movie_id,
                    success: (resultData) => addTitle(movie_id, resultData)
                });
                m.find("#quantity").text(movie_quantity);
                m.find("#price").text(movie_price);
                m.find("#title").attr('id', "title_"+movie_id);
                m.find("#price").attr('id', "price_"+movie_id);
                m.find("#quantity").attr('id', "quantity_"+movie_id);
                m.find("#b_add").attr('id', "b_add_"+movie_id);
                m.find("#b_minus").attr('id', "b_minus_"+movie_id);
                m.find("#b_remove").attr('id', "b_remove_"+movie_id);
                cart.append(m);
                let key = "cart_"+movie_id;
                document.getElementById("b_add_"+movie_id).addEventListener("click", function(){
                    let value = sessionStorage.getItem(key).split("_");
                    sessionStorage.setItem(key, (parseInt(value[0]) + 1).toString()+"_"+value[1]);
                    let newNum = parseInt($("#"+movie_id).find("#quantity_"+movie_id).text()) + 1;
                    total = total + parseInt(value[1]);
                    $('#total').text("Total Price: "+total.toString())
                    $("#"+movie_id).find("#quantity_"+movie_id).text(newNum.toString());
                    $.ajax("api/cart", {
                        method: "POST",
                        data: "item="+movie_id+"&req=add",
                    })
                });
                document.getElementById("b_minus_"+movie_id).addEventListener("click", function(){
                    let value = sessionStorage.getItem(key).split("_");
                    if (!(value[0]==="1")){
                        sessionStorage.setItem(key, (parseInt(value[0]) - 1).toString()+"_"+value[1]);
                        let newNum = parseInt($("#"+movie_id).find("#quantity_"+movie_id).text()) - 1;
                        total = total - parseInt(value[1]);
                        $('#total').text("Total Price: "+total.toString())
                        $("#"+movie_id).find("#quantity_"+movie_id).text(newNum.toString());
                    }
                    $.ajax("api/cart", {
                        method: "POST",
                        data: "item="+movie_id+"&req=minus",
                    })
                });
                document.getElementById("b_remove_"+movie_id).addEventListener("click", function(){
                    let value = sessionStorage.getItem(key).split("_");
                    total = total - parseInt(value[1])*parseInt(value[0]);
                    $('#total').text("Total Price: "+total.toString())
                    sessionStorage.removeItem(key)
                    $("#"+movie_id).remove();
                    $.ajax("api/cart", {
                        method: "POST",
                        data: "item="+movie_id+"&req=remove",
                    })
                });
            }
        }
        $('#total').text("Total Price: "+total.toString())
    }
})

function handleCartResult(resultData){
    // let cart = $("#cart_body");
    // if (resultData.length===0){
    //     $('#change_message').text("Empty Cart");
    //     $('#payment').attr('onclick', '');
    // } else {
    //     for (let i=0; i<resultData.length; i++){
    //         let m = template.clone();
    //         m.attr('id', resultData[i]["mid"]);
    //         m.find("#title").text(resultData[i]["title"]);
    //         m.find("#quantity").text(resultData[i]["quantity"]);
    //         m.find("#price").text(resultData[i]["price"]);
    //         m.find("#title").attr('id', "title_"+resultData[i]["mid"]);
    //         m.find("#price").attr('id', "price_"+resultData[i]["mid"]);
    //         m.find("#quantity").attr('id', "quantity_"+resultData[i]["mid"]);
    //         m.find("#b_add").attr('id', "b_add_"+resultData[i]["mid"]);
    //         m.find("#b_minus").attr('id', "b_minus_"+resultData[i]["mid"]);
    //         m.find("#b_remove").attr('id', "b_remove_"+resultData[i]["mid"]);
    //         cart.append(m);
    //         document.getElementById("b_add_"+resultData[i]["mid"]).addEventListener("click", function(){
    //             $.ajax("api/cart", {
    //                 method: "POST",
    //                 data: "item="+resultData[i]["mid"]+"&req=add",
    //                 success: function (resultData){
    //                     $('#change_message').text(resultData['message']);
    //                     if (resultData["status"]==="success"){
    //                         let s = $("#"+resultData["mid"]);
    //                         let n = parseInt(s.find("#quantity_"+resultData["mid"]).text(),10)+1;
    //                         s.find("#quantity_"+resultData["mid"]).text(n.toString());
    //                     }
    //                 }
    //             })
    //         });
    //         document.getElementById("b_minus_"+resultData[i]["mid"]).addEventListener("click", function(){
    //             $.ajax("api/cart", {
    //                 method: "POST",
    //                 data: "item="+resultData[i]["mid"]+"&req=minus",
    //                 success: function (resultData){
    //                     $('#change_message').text(resultData['message']);
    //                     if (resultData["status"]==="success"){
    //                         let s = $("#"+resultData["mid"]);
    //                         let n = parseInt(s.find("#quantity_"+resultData["mid"]).text(),10)-1;
    //                         s.find("#quantity_"+resultData["mid"]).text(n.toString());
    //                     }
    //                 }
    //             })
    //         });
    //         document.getElementById("b_remove_"+resultData[i]["mid"]).addEventListener("click", function(){
    //             $.ajax("api/cart", {
    //                 method: "POST",
    //                 data: "item="+resultData[i]["mid"]+"&req=remove",
    //                 success: function (resultData){
    //                     $('#change_message').text(resultData['message']);
    //                     if (resultData["status"]==="success"){
    //                         $("#"+resultData["mid"]).remove();
    //                     }
    //                 }
    //             })
    //         });
    //     }
    // }
}

