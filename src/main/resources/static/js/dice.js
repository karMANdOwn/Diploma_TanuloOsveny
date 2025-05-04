document.addEventListener('DOMContentLoaded', function () {
    const diceContainer = document.querySelector('.dice-container');
    const dice = document.querySelector('.dice');
    const rollButton = document.getElementById('rollButton');

    let isRolling = false;

    // Eddigi rotációk
    let totalRotationX = 0;
    let totalRotationY = 0;

    // Minden oldalhoz tartozó forgatási irány
    const diceRotation = {
        1: { x: 0, y: 0 },
        2: { x: 90, y: 0 },       // helyes: 2 alul
        3: { x: 0, y: -90 },
        4: { x: 0, y: 90 },
        5: { x: -90, y: 0 },      // helyes: 5 felül
        6: { x: 0, y: 180 }
    };

    function rollDice() {
        if (isRolling) return;
        isRolling = true;

        const randomValue = Math.floor(Math.random() * 6) + 1;
        console.log("Dobás eredménye: " + randomValue);

        // Extra teljes kör a látványos forgáshoz
        const extraSpinX = 360 * 2;
        const extraSpinY = 360 * 2;

        // Hozzáadjuk az új pozícióhoz tartozó forgatást
        totalRotationX += extraSpinX + diceRotation[randomValue].x;
        totalRotationY += extraSpinY + diceRotation[randomValue].y;

        // Forgatás
        dice.style.transform = `rotateX(${totalRotationX}deg) rotateY(${totalRotationY}deg)`;

        // Visszaengedés
        setTimeout(() => {
            isRolling = false;

            const diceRolledEvent = new CustomEvent('diceRolled', {
                detail: { value: randomValue }
            });
            document.dispatchEvent(diceRolledEvent);

            if (rollButton) {
                rollButton.disabled = true;
                setTimeout(() => {
                    rollButton.disabled = false;
                }, 1000);
            }
        }, 1000);
    }

    if (rollButton) {
        rollButton.addEventListener('click', rollDice);
    }

    if (diceContainer) {
        diceContainer.addEventListener('click', function () {
            if (!isRolling && (!rollButton || !rollButton.disabled)) {
                rollDice();
            }
        });
    }

    // Indulási állapot
    dice.style.transform = `rotateX(0deg) rotateY(0deg)`;
    window.rollDice = rollDice;
});
