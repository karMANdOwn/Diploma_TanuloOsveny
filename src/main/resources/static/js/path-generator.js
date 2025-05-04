// Ösvény generátor - azonos méretű mezőkkel, kanyargós úton
document.addEventListener('DOMContentLoaded', function () {
    // Csak akkor futtatjuk, ha a játéktáblán vagyunk
    const gameBoard = document.querySelector('.game-board');
    if (!gameBoard) return;

    // Kinyerjük a mezők adatait
    const boardFields = document.querySelectorAll('.board-field');
    if (!boardFields.length) return;

    // A mező mérete az eredetiből (ezek azonosak maradnak)
    const fieldSize = 80; // px
    const fieldMargin = 5; // px
    const totalFieldSize = fieldSize + 2 * fieldMargin;

    // Tábla konténer méretének beállítása
    const boardContainer = document.querySelector('.board-container');
    if (!boardContainer) return;

    // Eredeti mezők elrejtése
    boardFields.forEach(field => {
        field.style.display = 'none';
    });

    // Ösvény útvonal definiálása (relatív pozíciók)
    // A [0,0] a kezdőpont, és minden következő szám azt mutatja, hány mezőnyit mozdulunk el
    // x és y irányban az előző ponthoz képest
    const pathRoute = [
        [0, 0],    // 0. mező - kezdőpont
        [1, 0],    // 1. mező - jobbra 1
        [1, 0],    // 2. mező - jobbra 1
        [1, 0],    // 3. mező - jobbra 1
        [1, 0],    // 4. mező - jobbra 1
        [0, 1],    // 5. mező - lefelé 1
        [0, 1],    // 6. mező - lefelé 1
        [-1, 0],   // 7. mező - balra 1
        [-1, 0],   // 8. mező - balra 1
        [-1, 0],   // 9. mező - balra 1
        [-1, 0],   // 10. mező - balra 1
        [0, 1],    // 11. mező - lefelé 1
        [1, 0],    // 12. mező - jobbra 1
        [1, 0],    // 13. mező - jobbra 1
        [1, 0],    // 14. mező - jobbra 1
        [1, 0],    // 15. mező - jobbra 1
        [0, 1],    // 16. mező - lefelé 1
        [-1, 0],   // 17. mező - balra 1
        [-1, 0],   // 18. mező - balra 1
        [-1, 0],   // 19. mező - balra 1
        [-1, 0],   // 20. mező - balra 1
        [0, 1],    // 21. mező - lefelé 1
        [1, 0],    // 22. mező - jobbra 1
        [1, 0],    // 23. mező - jobbra 1
        [1, 0],    // 24. mező - jobbra 1
        [1, 0],    // 25. mező - jobbra 1
        [0, 1],    // 26. mező - lefelé 1
        [-1, 0],   // 27. mező - balra 1
        [-1, 0],   // 28. mező - balra 1
        [-1, 0],   // 29. mező - balra 1
        [-1, 0],   // 30. mező - balra 1
        [0, 1],    // 31. mező - lefelé 1
        [1, 0],    // 32. mező - jobbra 1
        [1, 0],    // 33. mező - jobbra 1
        [1, 0],    // 34. mező - jobbra 1
        [1, 0],    // 35. mező - jobbra 1
        [0, 1],    // 36. mező - lefelé 1
        [-1, 0],   // 37. mező - balra 1
        [-1, 0],   // 38. mező - balra 1
        [-1, 0],   // 39. mező - balra 1
        [-1, 0],   // 40. mező - balra 1
        [0, 1],    // 41. mező - lefelé 1
        [1, 0],    // 42. mező - jobbra 1
        [1, 0],    // 43. mező - jobbra 1
        [1, 0],    // 44. mező - jobbra 1
        [1, 0],    // 45. mező - jobbra 1
        [0, 1],    // 46. mező - lefelé 1
        [0, 1],    // 47. mező - lefelé 1
        [0, 1],    // 48. mező - lefelé 1
        [0, 1]     // 49. mező - lefelé 1 (célpont)
    ];

    // Maximális sorok és oszlopok számítása
    const maxCols = Math.max(...pathRoute.map((_, index) => {
        return pathRoute.slice(0, index + 1).reduce((sum, [x, _]) => sum + x, 0);
    })) + 1;

    const maxRows = Math.max(...pathRoute.map((_, index) => {
        return pathRoute.slice(0, index + 1).reduce((sum, [_, y]) => sum + y, 0);
    })) + 1;

    // Tábla méretének beállítása
    boardContainer.style.width = (maxCols * totalFieldSize) + 'px';
    boardContainer.style.height = (maxRows * totalFieldSize) + 'px';
    boardContainer.style.position = 'relative';

    // Ösvény mezőinek létrehozása
    const pathFields = [];
    let currentX = 0;
    let currentY = 0;

    pathRoute.forEach((route, index) => {
        // Jelenlegi abszoult pozíció számítása
        currentX += route[0];
        currentY += route[1];

        // Eredeti mező klónozása
        const originalField = boardFields[index];
        const newField = originalField.cloneNode(true);
        newField.style.display = 'block';
        newField.style.position = 'absolute';
        newField.style.left = (currentX * totalFieldSize) + 'px';
        newField.style.top = (currentY * totalFieldSize) + 'px';

        // Játékos pozíció áthelyezése, ha ez az aktuális mező
        const player = originalField.querySelector('#player');
        if (player) {
            // Eltávolítjuk az eredeti mezőből
            originalField.removeChild(player);
            // Hozzáadjuk az új mezőhöz
            newField.appendChild(player);
        }

        boardContainer.appendChild(newField);
        pathFields.push(newField);
    });

    // Vonalak rajzolása a mezők között (opcionális)
    for (let i = 0; i < pathFields.length - 1; i++) {
        const startField = pathFields[i];
        const endField = pathFields[i + 1];

        // Mezők középpontjának kiszámítása
        const startRect = startField.getBoundingClientRect();
        const endRect = endField.getBoundingClientRect();
        const containerRect = boardContainer.getBoundingClientRect();

        const startX = startRect.left + startRect.width / 2 - containerRect.left;
        const startY = startRect.top + startRect.height / 2 - containerRect.top;
        const endX = endRect.left + endRect.width / 2 - containerRect.left;
        const endY = endRect.top + endRect.height / 2 - containerRect.top;

        // Vonal hossza és szöge
        const length = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
        const angle = Math.atan2(endY - startY, endX - startX) * 180 / Math.PI;

        // Vonal létrehozása
        const line = document.createElement('div');
        line.style.position = 'absolute';
        line.style.width = `${length}px`;
        line.style.height = '4px';
        line.style.backgroundColor = '#666';
        line.style.left = `${startX}px`;
        line.style.top = `${startY}px`;
        line.style.transformOrigin = '0 0';
        line.style.transform = `rotate(${angle}deg)`;
        line.style.zIndex = '0';

        boardContainer.appendChild(line);
    }
});