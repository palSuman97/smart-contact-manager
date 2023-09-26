console.log("this is script file");

function togggleSidebar() {
    if ($(".sidebar").is(":visible")) {

        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    }
    else {

        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "22%");
    }
};

const searchContacts = () => {
    // console.log("searching.. ");

    let query = $("#search-input").val();
	
    if(query==""){
        $(".search-result").hide();
    }
    else{

        let url =`http://localhost:8080/searchContacts/${query}`;

        fetch(url)
            .then((response) => {
                return response.json();
            })
            .then((data) => {
                console.log(data);
                let text=`<div class='list-group'>`;

                data.forEach((contact) => {
                    text+=`<a href='/user/view_singleContact/+${contact.cid}' class='list-group-item list-group-action'> ${contact.name} </a>`;
                });

                text+=`</div>`;
                $(".search-result").html(text);
                $(".search-result").show();                
            });

        
    }
};