/**
 * excel_export.js
 * Utilidad compartida para manejar la exportación a Excel de forma asíncrona
 * mostrando un toast de carga amigable al usuario.
 */

// Estilos inyectados para el Toast de carga
const style = document.createElement('style');
style.textContent = `
    .excel-toast-overlay {
        position: fixed;
        bottom: 20px;
        right: 20px;
        background: #ffffff;
        border-radius: 8px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 12px 20px;
        z-index: 9999;
        font-family: 'Inter', system-ui, sans-serif;
        color: #333;
        border: 1px solid #e2e8f0;
        transform: translateY(100px);
        opacity: 0;
        transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275), opacity 0.3s;
    }

    .excel-toast-overlay.show {
        transform: translateY(0);
        opacity: 1;
    }

    .excel-spinner {
        width: 24px;
        height: 24px;
        border: 3px solid #e1e4e8;
        border-top-color: #28a745;
        border-radius: 50%;
        animation: excel-spin 1s linear infinite;
    }

    @keyframes excel-spin {
        to {
            transform: rotate(360deg);
        }
    }
    
    .excel-toast-content {
        display: flex;
        flex-direction: column;
    }
    
    .excel-toast-title {
        font-weight: 600;
        font-size: 0.9rem;
        margin-bottom: 2px;
    }
    
    .excel-toast-text {
        font-size: 0.8rem;
        color: #666;
    }
`;
document.head.appendChild(style);

/**
 * Función central para descargar el excel asíncronamente
 * @param {string} url - URL para hacer fetch de la exportación a Excel
 */
window.downloadExcelAsync = async function (url) {
    // 1. Crear e inyectar el toast
    const toast = document.createElement('div');
    toast.className = 'excel-toast-overlay';
    toast.innerHTML = `
        <div class="excel-spinner"></div>
        <div class="excel-toast-content">
            <span class="excel-toast-title">Exportando a Excel</span>
            <span class="excel-toast-text">Preparando tu archivo, por favor espera...</span>
        </div>
    `;
    document.body.appendChild(toast);

    // Animación de entrada
    requestAnimationFrame(() => {
        toast.classList.add('show');
    });

    try {
        // 2. Hacer la solicitud fetch
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error('Error al generar el archivo. Status: ' + response.status);
        }

        // 3. Extraer nombre de archivo de las cabeceras si es posible
        let filename = 'Reporte.xlsx';
        const disposition = response.headers.get('Content-Disposition');
        if (disposition && disposition.indexOf('attachment') !== -1) {
            const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
            const matches = filenameRegex.exec(disposition);
            if (matches != null && matches[1]) {
                filename = matches[1].replace(/['"]/g, '');
            }
        }

        // Extraer como Blob
        const blob = await response.blob();

        // Verifica que no estemos recibiendo un HTML de error oculto o un tamaño 0
        if (blob.size === 0) {
            throw new Error("El archivo exportado está vacío.");
        }

        // 4. Crear un enlace temporal para la descarga
        const windowUrl = window.URL || window.webkitURL;
        const downloadUrl = windowUrl.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = downloadUrl;
        a.download = filename;
        document.body.appendChild(a);
        a.click();

        // Limpiar recursos
        setTimeout(() => {
            windowUrl.revokeObjectURL(downloadUrl);
            document.body.removeChild(a);
        }, 100);

        // Cambiar icono del toast temporalmente para indicar éxito (opcional)
        const spinner = toast.querySelector('.excel-spinner');
        if (spinner) {
            spinner.className = ''; // remover clase de spin
            spinner.style.border = 'none';
            spinner.style.width = '24px';
            spinner.style.height = '24px';
            spinner.style.display = 'flex';
            spinner.style.alignItems = 'center';
            spinner.style.justifyContent = 'center';
            spinner.innerHTML = '<svg fill="#28a745" viewBox="0 0 20 20" style="width:24px; height:24px;"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"></path></svg>';
        }

        const titleText = toast.querySelector('.excel-toast-title');
        const descText = toast.querySelector('.excel-toast-text');
        if (titleText) titleText.textContent = "¡Descarga iniciada!";
        if (descText) descText.textContent = filename;

        // Ocultar y remover el toast después de 2.5s
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 300);
        }, 2500);

    } catch (error) {
        console.error("Error al exportar:", error);

        // Mostrar error en el toast
        const spinner = toast.querySelector('.excel-spinner');
        if (spinner) {
            spinner.className = '';
            spinner.style.border = 'none';
            spinner.style.width = '24px';
            spinner.style.height = '24px';
            spinner.style.display = 'flex';
            spinner.style.alignItems = 'center';
            spinner.style.justifyContent = 'center';
            spinner.innerHTML = '<svg fill="#dc3545" viewBox="0 0 20 20" style="width:24px; height:24px;"><path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path></svg>';
        }

        const titleText = toast.querySelector('.excel-toast-title');
        const descText = toast.querySelector('.excel-toast-text');
        if (titleText) {
            titleText.textContent = "Error al exportar";
            titleText.style.color = '#dc3545';
        }
        if (descText) descText.textContent = "Intenta de nuevo más tarde.";

        // Ocultarlo después de más tiempo
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.parentNode.removeChild(toast);
                }
            }, 300);
        }, 4000);
    }
};

// Autoinicializador para encontrar los enlaces que tengan la clase .export-single-btn
document.addEventListener('DOMContentLoaded', () => {
    // Delegación de eventos para manejar exportaciones individuales (incluso en tablas dinámicas)
    document.addEventListener('click', (e) => {
        const btn = e.target.closest('.export-single-btn');
        if (btn) {
            e.preventDefault();
            const href = btn.getAttribute('href');
            if (href) {
                window.downloadExcelAsync(href);
            }
        }
    });
});
