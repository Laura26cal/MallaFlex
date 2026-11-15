// Selector de materias con búsqueda: escribes un código o nombre, eliges de la lista y queda como "chip".
document.addEventListener('DOMContentLoaded', function () {
    var opciones = Array.prototype.slice.call(document.querySelectorAll('#lista-materias option'));

    document.querySelectorAll('.selector').forEach(function (sel) {
        var campo = sel.dataset.campo;
        var entrada = sel.querySelector('.selector-entrada');
        var chips = sel.querySelector('.chips');

        function yaEsta(codigo) {
            return Array.prototype.some.call(chips.querySelectorAll('input[type="hidden"]'), function (i) {
                return i.value === codigo;
            });
        }

        function agregar(opcion) {
            if (!opcion || yaEsta(opcion.dataset.codigo)) return;
            var chip = document.createElement('span');
            chip.className = 'chip';

            var texto = document.createElement('span');
            texto.textContent = opcion.value;

            var oculto = document.createElement('input');
            oculto.type = 'hidden';
            oculto.name = campo;
            oculto.value = opcion.dataset.codigo;

            var quitar = document.createElement('button');
            quitar.type = 'button';
            quitar.className = 'chip-x';
            quitar.textContent = '×';
            quitar.setAttribute('aria-label', 'Quitar');

            chip.appendChild(texto);
            chip.appendChild(oculto);
            chip.appendChild(quitar);
            chips.appendChild(chip);
        }

        function buscar(valor, soloExacto) {
            valor = valor.trim();
            return opciones.find(function (o) {
                return o.value === valor ||
                       (!soloExacto && o.dataset.codigo === valor.split(' · ')[0].trim());
            });
        }

        // al elegir una opción de la lista se agrega al instante
        entrada.addEventListener('input', function () {
            var o = buscar(entrada.value, true);
            if (o) { agregar(o); entrada.value = ''; }
        });

        // si escribió solo el código y confirmó con Enter o saliendo del campo
        entrada.addEventListener('change', function () {
            var o = buscar(entrada.value, false);
            if (o) agregar(o);
            entrada.value = '';
        });

        // Enter no debe enviar el formulario
        entrada.addEventListener('keydown', function (e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                entrada.dispatchEvent(new Event('change'));
            }
        });

        chips.addEventListener('click', function (e) {
            if (e.target.classList.contains('chip-x')) {
                e.target.closest('.chip').remove();
            }
        });
    });
});