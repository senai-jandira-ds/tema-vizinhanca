import "./style.module.css";
import Sidebar from "./components/sidebar";

function Dashboard() {

    return (
        <div className="screen-content">
            <Sidebar />
            <div className="test-content"></div>
        </div>
    )
}

export default Dashboard;