document.addEventListener('DOMContentLoaded', () => {
  const $decimal = document.getElementById('decimal');
  $decimal.value = Math.floor(10000 + Math.random() * 90000);
  convert(0);
});

const convert = (mode) => {
  const $decimal = document.getElementById('decimal');
  const $binary = document.getElementById('binary');
  const $octal = document.getElementById('octal');
  const $hexadecimal = document.getElementById('hexadecimal');

  const inputArr = [$decimal, $binary, $octal, $hexadecimal];
  const radixArr = [10, 2, 8, 16];

  const input = inputArr[mode];
  const radix = radixArr[mode];

  const orgNumber = parseInt(input.value, radix);

  if (isNaN(orgNumber)) {
    inputArr.forEach(input => input.value = '');
    return;
  }

  inputArr.forEach((input, index) => {
    if (index === mode) {
      return;
    }
    input.value = orgNumber.toString(radixArr[index]);
  });
};

const validate = (e, searchValue) => {
  e.target.value = e.target.value.replace(searchValue, '');
}

const validateDecimal = (e) => {
  validate(e, /[^0-9]/g);
};

const validateBinary = (e) => {
  validate(e, /[^0-1]/g);
};

const validateOctal = (e) => {
  validate(e, /[^0-7]/g);
};

const validateHexadecimal = (e) => {
  validate(e, /[^0-9a-fA-F]/g);
}
