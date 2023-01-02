import dynamic from "next/dynamic";
import {Spinner} from "react-bootstrap";
import React from "react";

const QuillNoSSRWrapper = dynamic(import('react-quill'), {
    ssr: false,
    loading: () => <div className="text-center">
        <Spinner animation="border" variant="secondary"/>
    </div>,
})


export const modules = {
    toolbar: [
        [{ header: '1' }, { header: '2' }, { header: '3' }, { font: [] }],
        [{ size: [] }],
        ['bold', 'italic', 'underline', 'strike', 'blockquote'],
        [
            { list: 'ordered' },
            { list: 'bullet' },
            { indent: '-1' },
            { indent: '+1' },
        ],
        ['link'],
        ['clean'],
    ],
    clipboard: {
        // toggle to add extra line breaks when pasting HTML:
        matchVisual: false,
    }
}

export const formats = [
    'header',
    'font',
    'size',
    'bold',
    'italic',
    'underline',
    'strike',
    'blockquote',
    'list',
    'bullet',
    'indent',
    'link',
    'image',
    'video'
];

export default QuillNoSSRWrapper;
