import React from "react";
import {Direction} from "../types/chat";
import {Spinner} from "react-bootstrap";

interface MessageProps {

    message?: string | null | undefined;
    icon: React.ReactNode;

    spinnerText?: string;

    loading?: boolean;

    multiRowEnabled?: boolean;

    messages?: string[];

    direction: Direction;

}

const Message: React.FC<MessageProps> = ({
                                             message,
                                             icon,
                                             direction,
                                             spinnerText,
                                             loading,
                                             multiRowEnabled,
                                             messages}) => {
    return (
      direction === Direction.RIGHT ? <div className="d-flex flex-row justify-content-end mb-4 pt-1">
          <div>
              <p className="small p-2 me-3 mb-1 text-white rounded-3 bg-primary">
                  {message}
              </p>
          </div>
          {icon}
      </div>:
          <div className="d-flex flex-row justify-content-start">
              {icon}
              <div>
                  {multiRowEnabled ? messages?.map((message: string, index) => (
                      <p
                          key={index}
                          className="small p-2 ms-3 mb-1 rounded-3"
                          style={{ backgroundColor: "#f5f6f7" }}
                      >
                          {(!message && loading) ? <span><Spinner size="sm"
                                                                  variant="secondary"
                                                                  className="me-1"
                                                                  animation="grow"/>{spinnerText}</span> : message}
                      </p>
                  )): <p
                      className="small p-2 ms-3 mb-1 rounded-3"
                      style={{ backgroundColor: "#f5f6f7" }}
                  >
                      {(!message && loading) ? <span><Spinner size="sm"
                                                              variant="secondary"
                                                              className="me-1"
                                                              animation="grow"/>{spinnerText}</span> : message}
                  </p>}
              </div>
          </div>
    );
}

export default Message;