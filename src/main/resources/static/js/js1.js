document.addEventListener("DOMContentLoaded", function () {
    const title = document.getElementById("title");
    const text = title.innerText;
    title.innerText = "";
    let index = 0;

    function displayNextLetter() {
        if (index < text.length) {
            title.innerText += text[index];
            index++;
            setTimeout(displayNextLetter, 200); // 200ms delay for each letter
        }
    }

  
    
    

    displayNextLetter();
});
