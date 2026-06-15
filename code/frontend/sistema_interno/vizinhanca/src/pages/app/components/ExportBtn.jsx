import styles from "./ExportBtn.module.css";

import { pdf } from "@react-pdf/renderer";

import ListOfResidents from "../../../pdf/ListOfResidents";
import ReportsPdf from "../../../pdf/ListOfReports";
import ServicesPdf from "../../../pdf/ListOfServices";
import ActivityListPdf from "../../../pdf/ActivityList";

export default function ExportButton({
    type,
    data,
    columns
}) {

    async function handleExport() {

        try {

            let PdfComponent;

            if (type === "moradores") {
                PdfComponent = ListOfResidents;
            }

            else if (type === "denuncias") {
                PdfComponent = ReportsPdf;
            }

            else if (type === "services") {
                PdfComponent = ServicesPdf;
            }

            else if (type === "atividade-geral") {
                PdfComponent = ActivityListPdf;
            }

            if (!PdfComponent) {
                console.error("Tipo de PDF inválido");
                return;
            }

            const blob = await pdf(
                <PdfComponent
                    data={data}
                    columns={columns}
                />
            ).toBlob();

            const url = URL.createObjectURL(blob);

            window.open(url, "_blank");

        } catch (err) {
            console.error(err);
        }
    }

    return (
        <div
            className={styles.exportButton}
            onClick={handleExport}
        >
            Exportar
        </div>
    );
}