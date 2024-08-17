"use client";

import JSEncrypt from "jsencrypt";
import { useEffect, useState } from "react";
export default function Home() {
  const [inputList, setInputList] = useState<string[]>([]);
  const [keys, setKeys] = useState<string[]>([]);
  const [keyPadId, setKeyPadId] = useState<string[]>([]);

  const [backgroundImg, setBackgroundImg] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    async function fetchData() {
      try {
        const res = await fetch("/api/keypad");
        const initialData = await res.json();

        setKeys(initialData.keys);
        setKeyPadId(initialData.keypadId);
        setBackgroundImg(`data:image/png;base64,${initialData.backgroundImg}`);
        setLoading(false);
      } catch (error) {
        console.error("Error fetching data:", error);
        setLoading(false);
      }
    }

    fetchData();
  }, []);

  useEffect(() => {
    if (inputList.length === 6) {
      handleSubmit();
    }
  }, [inputList]);

  const handleKeyClick = (key: string) => {
    if (key && inputList.length <= 6 && key != "") {
      setInputList([...inputList, key]);
      if (inputList.length === 6) {
        // handleSubmit();
      }
    }
  };

  const handleSubmit = async () => {
    var keyLength = inputList.length;
    var joinedKeys = inputList.join("");
    console.log(joinedKeys);
    //decrypt using ./private_key.pem
    var encrypt = new JSEncrypt(); // Create a new instance of the JSEncrypt library.
    var publicKey = `
-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtkLA7dcyLqz4M6BS/XZi
wMee85fjwskmxfZVN/qI854Sa4mlU/5Rse0HcNY0QoF+J3kQF3xWpTKLfw2p5pzt
sALLN6gsO2m4qLIOk3eNR+hVL2Rh4dc8MAhuXfoTGrfMjXouiy05rYgVpqIRRCjz
MVGYnJ7arZ6rMN73nRxd0I9RVbe3LXEuHrBysxjfXae6z+qb+1Rp9MKnwiDuKC/i
2lqqqmV9p/8OuY+qUzsMCtU8URS8kvw/bkg90TEOHzjKWrRIYRcQQkdJ8KuX3/lV
1jBBgIQRfmQVTFUnkV5XBZw9jXYTsz6Bcp4MNWUlwHQIebAM8vMZ6/nH9p4OdETA
5wIDAQAB
-----END PUBLIC KEY-----
`;
    encrypt.setPublicKey(publicKey);
    var encrypted = encrypt.encrypt(joinedKeys);
    console.log(encrypted);

    fetch("/api/keypad/submit", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        keypadId: keyPadId,
        data: encrypted,
        keyLength: keyLength,
      }),
    })
      .then((response) => response.json())
      .then((result) => {
        console.log(result);
        setInputList([]);
        window.location.reload();
        alert(JSON.stringify(result));
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("An error occurred");
      });
  };
  const handleClear = () => {
    setInputList([]);
  };

  const isDisabled = inputList.length === 6;

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-900 text-white">
      {loading ? (
        <div className="flex items-center justify-center h-screen">
          <div className="text-2xl font-semibold">Loading...</div>
        </div>
      ) : (
        <div className="text-center">
          <h1 className="text-3xl font-bold mb-4">Enter Your 6-Digit Code</h1>
          <div className="flex justify-center mb-6">
            {Array(6)
              .fill(null)
              .map((_, index) => (
                <span
                  key={index}
                  className={`w-8 h-8 mx-2 rounded-full ${
                    inputList.length > index ? "bg-white" : "bg-gray-800"
                  }`}
                />
              ))}
          </div>
          <div
            className={`grid grid-cols-4 grid-rows-3 gap-3 mx-auto relative ${
              isDisabled ? "blur-sm" : ""
            }`}
            style={{
              width: "400px",
              height: "300px",
              backgroundImage: `url(${backgroundImg})`,
              backgroundSize: "cover",
            }}
          >
            {keys.map((key, index) => (
              <button
                key={index}
                onClick={() => handleKeyClick(key)}
                className={`${
                  key ? "opacity-100" : "opacity-0"
                } w-full h-full border-none cursor-pointer ${
                  isDisabled ? "pointer-events-none" : ""
                }`}
                style={{
                  pointerEvents: key && !isDisabled ? "auto" : "none",
                }}
              >
                {key ? "" : ""}
              </button>
            ))}
          </div>
          <div className="mt-8 space-x-4">
            <button
              onClick={handleSubmit}
              className={`px-4 py-2 rounded-md text-white ${
                !isDisabled
                  ? "bg-blue-300 cursor-not-allowed"
                  : "bg-blue-500 hover:bg-blue-600"
              }`}
              disabled={!isDisabled}
            >
              Submit
            </button>
            <button
              onClick={handleClear}
              className="px-4 py-2 bg-red-500 text-white rounded-md hover:bg-red-600"
            >
              Clear
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
