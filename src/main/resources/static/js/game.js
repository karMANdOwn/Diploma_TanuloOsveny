// Modális ablak kezelése
document.addEventListener('DOMContentLoaded', function () {
    const rollButton = document.getElementById('rollButton');
    const playerScore = document.getElementById('playerScore');
    const challengeModal = document.getElementById('challengeModal');
    const modalChallengeQuestion = document.getElementById('modalChallengeQuestion');
    const modalChallengeImage = document.getElementById('modalChallengeImage');
    const modalChallengeSound = document.getElementById('modalChallengeSound');
    const modalOptionsContainer = document.getElementById('modalOptionsContainer');
    const modalAnswerButton = document.getElementById('modalAnswerButton');
    const modalHintButton = document.getElementById('modalHintButton');
    const modalHintText = document.getElementById('modalHintText');
    const modalBonusIndicator = document.getElementById('modalBonusIndicator');
    const gameFinished = document.getElementById('gameFinished');

    let victoryPlayed = false;
    let currentChallengeData = null;

    // Ha a játék befejeződött, hívjuk meg a finish végpontot
    if (gameFinished) {
        if (!victoryPlayed) {
            victoryPlayed = true;
            setTimeout(() => {
                playSound('/sounds/victory.mp3');
                // Irányítsuk át a felhasználót a finish végpontra
                window.location.href = `/game/${gameId}/finish`;
            }, 2000);
        }
    }

    function playSound(soundUrl) {
        const audio = new Audio(soundUrl);
        audio.play().catch(error => console.error('Hiba a hang lejátszásánál:', error));
    }

    if (rollButton) {
        rollButton.addEventListener('click', function () {
            // rollDice() a dice.js-ből
        });
    }

    if (modalAnswerButton) {
        modalAnswerButton.addEventListener('click', function () {
            submitAnswer();
        });
    }

    if (modalHintButton) {
        modalHintButton.addEventListener('click', function () {
            toggleHint();
        });
    }

    document.addEventListener('diceRolled', function (event) {
        const diceValue = event.detail.value;
        setTimeout(() => {
            rollDiceOnServer(diceValue);
        }, 500);
    });

    function sendSelectedAnswer(selectedValue) {
        fetch(`/game/${gameId}/select-answer`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `answer=${encodeURIComponent(selectedValue)}`
        })
            .then(response => response.json())
            .catch(error => console.error('Hiba a válasz kiválasztásánál:', error));
    }

    function rollDiceOnServer(value) {
        fetch(`/game/${gameId}/roll?value=${value}`)
            .then(response => response.json())
            .then(data => {
                setTimeout(() => {
                    movePlayer();
                }, 1000);
            })
            .catch(error => console.error('Hiba a dobásnál:', error));
    }

    function movePlayer() {
        fetch(`/game/${gameId}/move`)
            .then(response => response.json())
            .then(data => {
                const start = data.previousPosition;
                const end = data.newPosition;

                movePlayerStepByStep(start, end, () => {
                    setTimeout(() => {
                        updateGameState(data.gameState);
                    }, 500);
                });
            })
            .catch(error => console.error('Hiba a mozgásnál:', error));
    }

    function movePlayerStepByStep(start, end, callback) {
        const playerElement = document.getElementById('player');
        if (!playerElement) return;

        let current = start;
        const stepDirection = start < end ? 1 : -1;

        function step() {
            const currentField = document.getElementById(`field-${current}`);
            if (currentField?.contains(playerElement)) {
                currentField.removeChild(playerElement);
            }

            current += stepDirection;
            if ((stepDirection === 1 && current > end) || (stepDirection === -1 && current < end)) {
                callback();
                return;
            }

            const nextField = document.getElementById(`field-${current}`);
            if (nextField) {
                nextField.appendChild(playerElement);
            }

            setTimeout(step, 400);
        }

        step();
    }

    function submitAnswer() {
        const selectedRadio = document.querySelector('#modalAnswerForm input[name="answerOption"]:checked');
        if (!selectedRadio) {
            alert('Kérlek válassz egy választ!');
            return;
        }

        fetch(`/game/${gameId}/answer`, {
            method: 'POST'
        })
            .then(response => response.json())
            .then(isCorrect => {
                if (isCorrect) {
                    showFeedback(true);
                    setTimeout(() => {
                        hideModal();
                        // Frissítjük az oldalt, hogy a pontszám frissüljön és a következő dobásra felkészüljünk
                        window.location.reload();
                    }, 2000);
                } else {
                    showFeedback(false);
                }
            })
            .catch(error => console.error('Hiba a válasz ellenőrzésénél:', error));
    }

    function toggleHint() {
        modalHintText.classList.toggle('hidden');
    }

    function updateGameState(game) {
        // Ha a játék állapota "CHALLENGE", akkor megjelenítjük a modált
        if (game.gameState === "CHALLENGE") {
            showChallengeModal(game);
        } else if (game.gameState === "FINISHED") {
            // Új: Ha a játék befejeződött, irányítsuk a felhasználót a befejezés oldalra
            window.location.href = `/game/${gameId}/finish`;
        } else {
            window.location.reload();
        }
    }

    function showChallengeModal(gameData) {
        // Csak akkor lekérjük a játékállapotot, ha nem kaptuk meg paraméterként
        if (!gameData) {
            fetch(`/game/${gameId}`)
                .then(response => response.json())
                .then(data => {
                    displayChallengeModal(data);
                })
                .catch(error => console.error('Hiba a játékállapot lekérésénél:', error));
        } else {
            displayChallengeModal(gameData);
        }
    }

    function displayChallengeModal(gameData) {
        const currentPosition = gameData.player.position;
        const currentField = gameData.board[currentPosition];
        const challenge = currentField.challenge;
        currentChallengeData = challenge;

        // Bónusz mező kijelzése
        if (currentField.type === 'BONUS') {
            modalBonusIndicator.style.display = 'block';
        } else {
            modalBonusIndicator.style.display = 'none';
        }

        // Kérdés beállítása
        modalChallengeQuestion.textContent = challenge.question;

        // Kép beállítása, ha van
        if (challenge.imageUrl) {
            modalChallengeImage.src = challenge.imageUrl;
            modalChallengeImage.style.display = 'block';
        } else {
            modalChallengeImage.style.display = 'none';
        }

        // Hang beállítása, ha van
        if (challenge.soundUrl) {
            modalChallengeSound.src = challenge.soundUrl;
            modalChallengeSound.style.display = 'block';
        } else {
            modalChallengeSound.style.display = 'none';
        }

        // Válaszlehetőségek generálása
        modalOptionsContainer.innerHTML = '';
        challenge.options.forEach((option, index) => {
            const optionItem = document.createElement('div');
            optionItem.className = 'option-item';

            const radio = document.createElement('input');
            radio.type = 'radio';
            radio.name = 'answerOption';
            radio.id = `modal-option-${index}`;
            radio.value = option;
            radio.checked = gameData.selectedAnswer === option;
            radio.addEventListener('change', () => sendSelectedAnswer(option));

            const label = document.createElement('label');
            label.setAttribute('for', `modal-option-${index}`);
            label.textContent = option;

            optionItem.appendChild(radio);
            optionItem.appendChild(label);
            modalOptionsContainer.appendChild(optionItem);
        });

        // Segítség beállítása
        modalHintText.textContent = challenge.hint;
        modalHintText.classList.add('hidden');

        // Modal megjelenítése
        challengeModal.style.display = 'flex';
    }

    function hideModal() {
        challengeModal.style.display = 'none';
    }

    function showFeedback(isCorrect) {
        const feedbackDiv = document.createElement('div');
        feedbackDiv.className = isCorrect ? 'feedback correct' : 'feedback incorrect';
        feedbackDiv.textContent = isCorrect ? 'Helyes válasz! 🎉' : 'Helytelen válasz! Próbáld újra! 🤔';

        document.body.appendChild(feedbackDiv);

        // Hang lejátszása
        playSound(isCorrect ? '/sounds/correct.mp3' : '/sounds/incorrect.mp3');

        setTimeout(() => {
            feedbackDiv.remove();
        }, 2000);
    }

    // Stílusok a visszajelzéshez
    const style = document.createElement('style');
    style.innerHTML = `
        .feedback {
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            padding: 20px;
            border-radius: 10px;
            color: white;
            font-size: 1.5em;
            font-weight: bold;
            z-index: 2000;
            animation: fadeIn 0.3s ease-in-out;
        }

        .correct {
            background-color: #4caf50;
        }

        .incorrect {
            background-color: #f44336;
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }
    `;
    document.head.appendChild(style);

    // Automatikusan megjeleníti a kihívást, ha az oldal betöltésekor CHALLENGE állapotban vagyunk
    if (window.gameState === "CHALLENGE") {
        showChallengeModal();
    } else if (window.gameState === "FINISHED") {
        // Új: ha az oldal betöltésekor FINISHED állapotban vagyunk, irányítsuk a felhasználót a befejezés oldalra
        setTimeout(() => {
            window.location.href = `/game/${gameId}/finish`;
        }, 2000);
    }
});