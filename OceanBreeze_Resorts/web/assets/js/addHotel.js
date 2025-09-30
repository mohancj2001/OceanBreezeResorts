async function addHotel() {
    const hotelId = document.getElementById("hotelSelect").value;

    const data = new FormData();

    data.append("locationId", hotelId); 

    const response = await fetch(
        "AddHotel",
        {
            method: "POST",
            body: data
        }
    );

    if (response.ok) {
        const json = await response.json();

        if (json.success) {
            alert(json.content);
            console.log(json.content);
            window.location.href = "add-hotel.html";
        }else{

            console.log("Error");
            alert("Error: Try Again later.");
        }
    }
}


async function loadHotel() {
    try {
        const response = await fetch("LoadHotel2");

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);  
        }

        const json = await response.json();
        const hotelList = json.hotelList;

        loadHotelSelect("hotelSelect", hotelList, "city");

    } catch (error) {
        console.error("Error loading hotels:", error);
        alert("Error loading hotels. Please try again later."); 
    }
}

function loadHotelSelect(selectTagId, list, property) {
    const selectTag = document.getElementById(selectTagId);
    selectTag.innerHTML = ""; 

    list.forEach(item => {
        let optionTag = document.createElement("option");
        optionTag.value = item.id;
        optionTag.innerHTML = item[property];
        selectTag.appendChild(optionTag);
    });
}