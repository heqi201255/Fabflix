
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    var urls = new URL(url);
    var c = urls.searchParams.get(target);
    console.log(c);
    return c;
}

let total = 0;
function handleStarResult(resultData) {
    //console.log(resultData.length);

    sessionStorage.setItem("returnPage", window.location.pathname+window.location.search)
    console.log(resultData.length);
    if(resultData.length == 0){
        return;
    }
    total = resultData[0]["movie_count"];
    //window.location.href = url.toString();
    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");

    for (let i = 0; i <resultData.length; i++) {
        //console.log(resultData[i]);

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" +
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_name"] +
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";

        rowHTML += "<th>";
        if(resultData[i]["movie_genre"] == null){

        }
        else{
        for (let m = 0; m < resultData[i]["movie_genre"].split(",").length; m++)
        {
            rowHTML += '<a href = "movies.html?gid='+ resultData[i]["movie_gid"].split(",")[m] +'">'
                + resultData[i]["movie_genre"].split(',')[m] + '</a>';
            if( m !== resultData[i]["movie_genre"].split(",").length - 1)
            {
                rowHTML += ",";
            }
        }
        rowHTML += "</th>";}

        rowHTML += "<th>";
        if (resultData[i]["movie_stars"] == null){
        }
        else{
        for (let m = 0; m < resultData[i]["movie_stars"].split(",").length; m++)
        {
            rowHTML += '<a href = "single-star.html?id='+ resultData[i]["star_id"].split(",")[m] +'">'
                + resultData[i]["movie_stars"].split(',')[m] + '</a>';
            if( m !== resultData[i]["movie_stars"].split(",").length - 1)
            {
                rowHTML += ",";
            }
        }
        rowHTML += "</th>";}
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "<th><button id='addCartButton_"+resultData[i]["movie_id"]+"'>Add to Cart</button></th>";
        rowHTML += "</tr>";
        starTableBodyElement.append(rowHTML);

        document.getElementById("addCartButton_"+resultData[i]["movie_id"]).addEventListener("click", function(){
            // $.ajax("api/cart", {
            //     method: "POST",
            //     data: "item="+resultData[i]["movie_id"]+"&req=add",
            //     success: function (resultData){
            //         alert(resultData['message']);
            //     }
            // })
            let key = "cart_"+resultData[i]["movie_id"]
            if (sessionStorage.getItem(key)){
                let value = sessionStorage.getItem(key).split("_")
                sessionStorage.setItem(key, (parseInt(value[0]) + 1).toString() +"_" + value[1]);
            } else {
                sessionStorage.setItem(key, (1).toString() + "_" + (Math.round(Math.random() * 100)).toString());
            }

            if (!sessionStorage.getItem("cartNum")){
                sessionStorage.setItem("cart_"+resultData[i]["movie_id"], (1).toString()+"_"+Math.round(Math.random() * 100));
            } else {
                sessionStorage.setItem("cartNum", (parseInt(sessionStorage.getItem("cartNum"))+1).toString())
            }
            alert("Successfully added item")
        });
    }
}


function getSort(){
    var x = document.getElementById("sortbox");
    var opt = x.selectedIndex;
    var value = x.options[opt].value;
    console.log(value);
    let url = new URL(window.location.href);
    url.searchParams.set('sort', value);

    window.location.href = url.toString();

    document.getElementById('sortbox').value = value;
    //console.log(url)
    console.log(url);
}

function getPage(){
    var x = document.getElementById("pagenumbox");
    var opt = x.selectedIndex;
    var value = x.options[opt].value;
    console.log(value);
    let url = new URL(window.location.href);
    url.searchParams.set('pagenum', value);

    window.location.href = url.toString();
}

function goPrev(){
    var url = window.location.search;
    var urlParams = new URLSearchParams(url);
    var pnum = urlParams.get("pagenum");
    var offsets = urlParams.get("offset");
    var new_offset = Number(offsets) - Number(pnum);

    if (new_offset >= 0)
    {
        //new_offset = 0;
        let new_url = new URL(window.location.href);
        new_url.searchParams.set('offset', new_offset.toString());
        window.location.href = new_url.toString();
    }

    console.log(new_offset);
    console.log(pnum);
    console.log(offsets);
}

function goNext() {
    console.log(total);
    var url = window.location.search;
    var urlParams = new URLSearchParams(url);
    var pnum = urlParams.get("pagenum");
    var offsets = urlParams.get("offset");
    var new_offset = Number(offsets) + Number(pnum);

    if (new_offset < total)
    {
         let new_url = new URL(window.location.href);
         new_url.searchParams.set('offset', new_offset.toString());
         console.log(url);
         window.location.href = new_url.toString();
    }
    console.log(url);
    console.log(pnum);
    console.log(offsets);
    console.log(new_offset);
}


let sort = getParameterByName("sort");
if (sort==null){sort="r_dsc_t_asc"}

let pnumber = getParameterByName("pagenum")
if (pnumber==null){pnumber="25"}

let offsets = getParameterByName("offset")
if (offsets==null){offsets="0"}

let gid = getParameterByName("gid");


let title = getParameterByName("title");

let singleSearch = getParameterByName("search");

let mtitle = getParameterByName("mtitle");
let year = getParameterByName("year");
let director = getParameterByName("director");
let sname = getParameterByName("sname");

$.ajax("api/jump", {
    method: 'POST',
    data: "url="+window.location.pathname.toString()+window.location.search.toString(),
})

if (gid!=null){
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/browse-genre?gid=" + gid + "&sort=" + sort + "&pagenum=" + pnumber + "&offset=" + offsets, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
} else if (title!=null){
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/browse-title?title=" + title +"&sort=" + sort + "&pagenum=" + pnumber + "&offset=" + offsets, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
} else if (singleSearch!=null){
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/new-search?search="+singleSearch +"&sort=" + sort + "&pagenum=" + pnumber + "&offset=" + offsets,
        success: (resultData) => handleStarResult(resultData)
    });
} else {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/multi-search?title="+mtitle+"&year="+year+"&director="+director+"&sname="+sname+"&sort=" +sort+ "&pagenum=" + pnumber + "&offset=" + offsets,
        success: (resultData) => handleStarResult(resultData)
    });
}