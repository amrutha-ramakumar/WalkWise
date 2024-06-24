const form = document.getElementById('addProduct');
const productName = document.getElementById('product_name');
const description = document.getElementById('description');
const quantity = document.getElementById('available_quantity');
const price = document.getElementById('price');


const setError = (element, message, e) => {
    console.log(element)
    e.preventDefault();
    const inputControl = element.parentElement;
    const errorDisplay = inputControl.querySelector('.error');

    errorDisplay.innerText = message;
    inputControl.classList.add('error');
    inputControl.classList.remove('success');
};
const setSuccess = element => {
    console.log(element)
    const inputControl = element.parentElement;
    const errorDisplay = inputControl.querySelector('.error');
    errorDisplay.innerText = '';
    inputControl.classList.add('success');
    inputControl.classList.remove('error');
};


function validateInputs(e) {
    setSuccess(productName);
    setSuccess(description);
    setSuccess(quantity);
    setSuccess(price);


    const productNameValue = productName.value.trim();
    const descriptionValue = description.value.trim();
    const quantityValue = quantity.value.trim();
    const priceValue = price.value.trim();

    if(productNameValue === '') {
        setError(productName, 'Please enter Product name', e);
        productName.focus();
        return false;
    }
    else{
        setSuccess(productName);
    }
    if(descriptionValue === '') {
        setError(description, 'Please enter Desccription', e);
        description.focus();
        return false;
    }
    else{
        setSuccess(description);
    }

    if(quantityValue === '') {
        setError(quantity, 'Please enter Quantity', e);
        quantity.focus();
        return false;
    }
    else{
        setSuccess(quantity);
    }


    if(priceValue === ""){
        setError(price, 'Enter Price', e);
        price.focus();
        return false;
    }
    else{
        setSuccess(price);
    }

    return true;
}

form.addEventListener('submit', function(e) {
    console.log("form submitted")
    if(validateInputs(e)){
        console.log("VALIDATION Success");
    }
})