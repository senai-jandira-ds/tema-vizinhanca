const [modalOpen, setModalOpen] = useState(false);
const [cellData, setCellData] = useState(null);

const handleCliqueCelula = (valor, colunaId, linha) => {
    setCellData({
      valor,
      colunaId,
      linha
    });
  
    setModalOpen(true);
  };