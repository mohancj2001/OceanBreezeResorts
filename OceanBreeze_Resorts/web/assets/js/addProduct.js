

async function loadRoom() {
    console.log("LoadRoomTypes");
    const response = await fetch(
        "LoadRoomTypes",
    );

    if (response.ok) {
        console.log("LoadRoomTypes");
        const json = await response.json();
        const roomList = json.roomList;

        loadRoomSelect("roomSelect", roomList, "room_type");
    }

}

function loadRoomSelect(selectTagId, list, property) {
    const selectTag = document.getElementById(selectTagId);
    list.forEach(item => {
        let optionTag = document.createElement("option");
        optionTag.value = item.id;
        optionTag.innerHTML = item[property];
        selectTag.appendChild(optionTag);
    });
}

async function loadHotel() {
    console.log("LoadHotel");
    const response = await fetch(
        "LoadHotel",
    );

    if (response.ok) {
        console.log("LoadHotel");
        const json = await response.json();
        const hotelList = json.hotelList;

        loadHotelSelect("hotelSelect", hotelList, "city");
    }

}

function loadHotelSelect(selectTagId, list, property) {
    const selectTag = document.getElementById(selectTagId);
    list.forEach(item => {
        console.log("Hotel:"+item.locations.id+" "+item.locations.city);
        let optionTag = document.createElement("option");
        optionTag.value = item.locations.id;
        optionTag.innerHTML = item.locations.city;
        selectTag.appendChild(optionTag);
    });
}

async function roomlisting() {
    const hotelSelectTag = document.getElementById("hotelSelect");
    const roomSelectTag = document.getElementById("roomSelect");
    const titleTag = document.getElementById("title");
    const descriptionTag = document.getElementById("description");
    const priceTag = document.getElementById("price");
    const quantityTag = document.getElementById("qty");
    const image1Tag = document.getElementById("image1");
    const image2Tag = document.getElementById("image2");
    const image3Tag = document.getElementById("image3");

    const data = new FormData();

    data.append("hotelId", hotelSelectTag.value);
    data.append("roomId", roomSelectTag.value);
    data.append("title", titleTag.value);
    data.append("description", descriptionTag.value);
    data.append("price", priceTag.value);
    data.append("quantity", quantityTag.value);
    data.append("image1", image1Tag.files[0]);
    data.append("image2", image2Tag.files[0]);
    data.append("image3", image3Tag.files[0]);

    console.log(data);

    const response = await fetch(
        "AddProduct",
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
            window.location.href = "add-product.html";
        }else{

            console.log("Error");
            alert("Error: Try Again later.");
        }
    }
}