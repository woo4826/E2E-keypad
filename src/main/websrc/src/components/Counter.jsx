export default function Counter({ count, onButtonPressed }) {
  return (
    <>
      <div>
        <p>Count: {count}</p>
        <button onClick={onButtonPressed}>Increment</button>
      </div>
    </>
  );
}
