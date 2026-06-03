import "./App.css";

import {
  BrowserRouter,
  Routes,
  Route,
  Navigate,
  useLocation,
} from "react-router-dom";

import { AnimatePresence } from "framer-motion";

import Login from "./pages/login";
import Sign from "./pages/cadastro";

import AppLayout from "./pages/app/AppLayout";
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Dashboard from "./pages/app/Dashboard";
import Activity from "./pages/app/Activity";
import Users from "./pages/app/Users";
import Services from "./pages/app/Services";
import Security from "./pages/app/Security";
import Settings from "./pages/app/Settings";
import Information from "./pages/app/Information";
import Categories from "./pages/app/Categories";
import Blocks from "./pages/app/Blocks";

function AnimatedRoutes() {
  const location = useLocation();

  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/" element={<Login />} />
        <Route path="/cadastro" element={<Sign />} />

        <Route path="/app" element={<AppLayout />}>
          <Route index element={<Dashboard />} />

          <Route path="activity" element={<Activity />} />
          <Route path="users" element={<Users />} />
          <Route path="services" element={<Services />} />
          <Route path="security" element={<Security />} />

          <Route path="settings" element={<Settings />}>
            <Route index element={<Navigate to="information" replace />} />

            <Route path="information" element={<Information />} />
            <Route path="categories" element={<Categories />} />
            <Route path="blocks" element={<Blocks />} />
          </Route>
        </Route>
      </Routes>
    </AnimatePresence>
  );
}

function App() {
  return (
    <BrowserRouter>
      <ToastContainer
        position="top-right"
        autoClose={5000}
        hideProgressBar={false}
        theme="colored"
      />
      <AnimatedRoutes />
    </BrowserRouter>
  );
}

export default App;