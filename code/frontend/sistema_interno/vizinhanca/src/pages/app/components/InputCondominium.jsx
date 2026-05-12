import React from "react";
import styles from "./InputCondominium.css";

export default function Input({
  placeholder = "Digite algo...",
  value,
  onChange,
}) {
  return (
    <div className="inputWrapper">
      <input
        className="custom-input"
        type="text"
        placeholder={placeholder}
        value={value}
        onChange={onChange}
      />
    </div>
  );
}