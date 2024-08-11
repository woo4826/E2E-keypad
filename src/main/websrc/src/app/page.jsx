"use client";

import React, {useEffect} from 'react';
import useCounter from "@/hooks/useCounter.jsx";
import Counter from "@/components/Counter.jsx";

export default function Page() {
  const { states, actions } = useCounter();

  useEffect(() => {
    actions.axiosSample()
    console.log("페이지 진입하면서 최초에 딱 1번만 실행되어야 하는 코드")
  }, []);

  console.log("렌더링 될 때 마다 호출되는 코드")

  return (
    <div>
      <Counter count={states.count} onButtonPressed={actions.increase}/>
    </div>
  )
}
